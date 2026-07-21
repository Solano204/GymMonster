export type HttpMethod = "GET" | "POST" | "PUT" | "DELETE" | "PATCH";

export interface EndpointDef {
  method: HttpMethod;
  path: string; // e.g. "/api/admin/clients/{username}"
  group: string; // e.g. "Admin - Clientes"
}

// Mirrors com.monster.gateway.Entities.Token
export interface Token {
  username: string;
  accessToken: string;
  refreshToken: string;
  expiresAt: string;
  refreshExpiresAt: string;
}
