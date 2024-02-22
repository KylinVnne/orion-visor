import type { ISftpTransferManager, ISftpTransferUploader, SftpTransferItem } from '../types/terminal.type';
import { TransferOperatorResponse } from '../types/terminal.type';
import { TransferReceiverType, TransferStatus, TransferType } from '../types/terminal.const';
import { Message } from '@arco-design/web-vue';
import { getTerminalAccessToken } from '@/api/asset/host-terminal';
import SftpTransferUploader from '@/views/host/terminal/handler/sftp-transfer-uploader';

export const wsBase = import.meta.env.VITE_WS_BASE_URL;

// sftp 传输管理器实现
export default class SftpTransferManager implements ISftpTransferManager {

  private client?: WebSocket;

  private run: boolean;

  private currentItem?: SftpTransferItem;

  private currentUploader?: ISftpTransferUploader;

  public transferList: Array<SftpTransferItem>;

  constructor() {
    this.run = false;
    this.transferList = [];
  }

  // 添加传输
  addTransfer(items: Array<SftpTransferItem>): void {
    this.transferList.push(...items);
    // 开始传输
    if (!this.run) {
      this.openClient();
    }
  }

  // 取消传输
  cancelTransfer(fileId: string): void {
    const index = this.transferList.findIndex(s => s.fileId === fileId);
    if (index === -1) {
      return;
    }
    const item = this.transferList[index];
    if (item.status === TransferStatus.TRANSFERRING) {
      // 传输中则中断传输
      if (this.currentUploader) {
        this.currentUploader.uploadAbort();
      }
    }
    // 从列表中移除
    this.transferList.splice(index, 1);
  }

  // 打开会话
  private async openClient() {
    this.run = true;
    // 获取 access
    const { data: accessToken } = await getTerminalAccessToken();
    // 打开会话
    this.client = new WebSocket(`${wsBase}/host/transfer/${accessToken}`);
    this.client.onerror = event => {
      // 打开失败将传输列表置为失效
      Message.error('会话打开失败');
      console.error('error', event);
      // 将等待中和传输中任务修改为失败状态
      this.transferList.filter(s => {
        return s.status === TransferStatus.WAITING
          || s.status === TransferStatus.TRANSFERRING;
      }).forEach(s => {
        s.status = TransferStatus.ERROR;
      });
    };
    this.client.onclose = event => {
      // 关闭会话重置 run
      this.run = false;
      console.warn('close', event);
    };
    this.client.onopen = () => {
      // 打开后自动传输下一个任务
      this.transferNextItem();
    };
    this.client.onmessage = this.resolveMessage.bind(this);
  }

  // 传输下一条任务
  private transferNextItem() {
    this.currentUploader = undefined;
    // 获取任务
    this.currentItem = this.transferList.find(s => s.status === TransferStatus.WAITING);
    if (this.currentItem) {
      // 开始传输
      if (this.currentItem.type === TransferType.UPLOAD) {
        // 上传
        this.uploadFile();
      } else {
        // 下载
        this.uploadDownload();
      }
    } else {
      // 无任务关闭会话
      this.client?.close();
    }
  }

  // 接收消息
  private async resolveMessage(message: MessageEvent) {
    const data = JSON.parse(message.data) as TransferOperatorResponse;
    if (data.type === TransferReceiverType.NEXT_BLOCK) {
      // 接收下一块上传数据
      await this.resolveNextBlock();
    } else if (data.type === TransferReceiverType.NEXT_TRANSFER) {
      // 接收接收下一个传输任务处理完成
      this.resolveNextTransfer(data);
    }
  }

  // 上传文件
  private uploadFile() {
    // 创建上传器
    this.currentUploader = new SftpTransferUploader(this.currentItem as SftpTransferItem, this.client as WebSocket);
    // 开始上传
    this.currentUploader.startUpload();
  }

  // 下载文件
  private uploadDownload() {
    // TODO
  }

  // 接收下一块上传数据
  private async resolveNextBlock() {
    // 只可能为上传并且成功
    if (!this.currentUploader) {
      return;
    }
    if (this.currentUploader.hasNextBlock()
      && !this.currentUploader.abort
      && !this.currentUploader.finish) {
      try {
        // 有下一个分片则上传 (上一个分片传输完成)
        await this.currentUploader.uploadNextBlock();
      } catch (e) {
        // 读取文件失败
        this.currentUploader.uploadError((e as Error).message);
      }
    } else {
      // 没有下一个分片则发送完成
      this.currentUploader.uploadFinish();
    }
  }

  // 接收下一个传输任务
  private resolveNextTransfer(data: TransferOperatorResponse) {
    if (this.currentItem) {
      if (data.success) {
        this.currentItem.status = TransferStatus.SUCCESS;
      } else {
        this.currentItem.status = TransferStatus.ERROR;
        this.currentItem.errorMessage = data.msg || '上传失败';
      }
    }
    // 开始下一个传输任务
    this.transferNextItem();
  }

}