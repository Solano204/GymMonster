import { http } from "@/lib/http";

/** Mirrors com.monster.gateway.Entities.Token. */
export interface Token {
  username: string;
  accessToken: string;
  refreshToken: string;
  expiresAt: string;
  refreshExpiresAt: string;
}

// SessionController takes credentials as plain headers, not a JSON body -
// see gateway/src/main/java/com/monster/gateway/Controller/SessionController.java.
export const authApi = {
  login: (username: string, password: string) =>
    http
      .post<Token>("/GymMonster/auth/login", undefined, { headers: { username, password } })
      .then((r) => r.data),

  logout: (username: string, refreshToken: string) =>
    http
      .post<string>("/GymMonster/auth/logout", undefined, {
        headers: { username, refresh_token: refreshToken },
      })
      .then((r) => r.data),
};
