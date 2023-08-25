import axios from 'axios';
import { DataGrid, Pagination } from '@/types/global';

/**
 * 角色创建请求
 */
export interface RoleCreateRequest {
  name?: string;
  code?: string;
  status?: number;
}

/**
 * 角色更新请求
 */
export interface RoleUpdateRequest extends RoleCreateRequest {
  id: number;
}

/**
 * 角色 菜单绑定请求
 */
export interface RoleMenuBindRequest extends RoleCreateRequest {
  roleId: number;
  menuIdList: Array<number>;
}

/**
 * 角色查询请求
 */
export interface RoleQueryRequest extends Pagination {
  id?: number;
  name?: string;
  code?: string;
  status?: number;
}

/**
 * 角色查询响应
 */
export interface RoleQueryResponse {
  id?: number;
  name?: string;
  code?: string;
  status?: number;
  createTime: number;
  updateTime: number;
  creator: string;
  updater: string;
}

/**
 * 创建角色
 */
export function createRole(request: RoleCreateRequest) {
  return axios.post('/infra/system-role/create', request);
}

/**
 * 通过 id 更新角色
 */
export function updateRole(request: RoleUpdateRequest) {
  return axios.put('/infra/system-role/update', request);
}

/**
 * 通过 id 更新角色状态
 */
export function updateRoleStatus(request: RoleUpdateRequest) {
  return axios.put('/infra/system-role/update-status', request);
}

/**
 * 通过 id 查询角色
 */
export function getRole(id: number) {
  return axios.get<RoleQueryResponse>('/infra/system-role/get', { params: { id } });
}

/**
 * 查询所有角色
 */
export function getRoleList() {
  return axios.get<RoleQueryResponse[]>('/infra/system-role/list');
}

/**
 * 分页查询角色
 */
export function getRolePage(request: RoleQueryRequest) {
  return axios.post<DataGrid<RoleQueryResponse>>('/infra/system-role/query', request);
}

/**
 * 通过 id 删除角色
 */
export function deleteRole(id: number) {
  return axios.delete('/infra/system-role/delete', { params: { id } });
}

/**
 * 绑定角色菜单
 */
export function bindRoleMenu(request: RoleMenuBindRequest) {
  return axios.put('/infra/system-role/bind', request);
}

/**
 * 获取角色菜单id
 */
export function getRoleMenuId(roleId: number) {
  return axios.get<Array<number>>('/infra/system-role/get-menu-id', { params: { roleId } });
}
