import { http } from "@/lib/http";
import type {
  AllClient,
  ClientDetailUpdateInput,
  ClientInfo,
  ClientRegisterInput,
  ClientRosterEntry,
  Membership,
  Pool,
  Promotion,
  Schedule,
  Specialty,
  TrainerListItem,
  WorkClass,
} from "@/types/api";

const BASE = "/api/page";

export interface Page {
  page?: number;
  size?: number;
}

// ---------------------------------------------------------------------------
// Memberships — RouterMembership
// ---------------------------------------------------------------------------

export const membershipsApi = {
  list: () => http.get<Membership[]>(`${BASE}/allMemberships`).then((r) => r.data),
};

// ---------------------------------------------------------------------------
// Pools — RouterPool
// ---------------------------------------------------------------------------

export const poolsApi = {
  list: () => http.get<Pool[]>(`${BASE}/allPools`).then((r) => r.data),
};

// ---------------------------------------------------------------------------
// Specialties — RouterSpecialty
// ---------------------------------------------------------------------------

export const specialtiesApi = {
  list: () => http.get<Specialty[]>(`${BASE}/allSpecialties`).then((r) => r.data),
};

// ---------------------------------------------------------------------------
// Promotions — RouterPromotion
// ---------------------------------------------------------------------------

export const promotionsApi = {
  current: (currentDate: string) =>
    http.get<Promotion[]>(`${BASE}/promotions/currentPromotions/${currentDate}`).then((r) => r.data),
  // Note: the route declares a {date} path segment, but the handler actually
  // reads a `date` query param. We send both so this keeps working if the
  // backend is fixed to read the path variable instead.
  byDate: (date: string) =>
    http
      .get<Promotion[]>(`${BASE}/promotions/specificDate/${date}`, { params: { date } })
      .then((r) => r.data),
};

// ---------------------------------------------------------------------------
// Work classes — RouterWorkClass
// ---------------------------------------------------------------------------

export const workClassesApi = {
  list: () => http.get<WorkClass[]>(`${BASE}/workclasses`).then((r) => r.data),
  schedules: (name: string) =>
    http.get<Schedule[]>(`${BASE}/workclasses/${encodeURIComponent(name)}/schedules`).then((r) => r.data),
  clients: (name: string, { page = 0, size = 10 }: Page = {}) =>
    http
      .get<ClientRosterEntry[]>(`${BASE}/workclasses/${encodeURIComponent(name)}/clients`, {
        params: { page, size },
      })
      .then((r) => r.data),
  trainers: (name: string, { page = 0, size = 10 }: Page = {}) =>
    http
      .get<ClientRosterEntry[]>(`${BASE}/workclasses/${encodeURIComponent(name)}/trainers`, {
        params: { page, size },
      })
      .then((r) => r.data),
};

// ---------------------------------------------------------------------------
// Trainers — preTrainers router
// ---------------------------------------------------------------------------

export const trainersApi = {
  list: ({ page = 0, size = 10 }: Page = {}) =>
    http.get<TrainerListItem[]>(`${BASE}/allTrainers`, { params: { page, size } }).then((r) => r.data),
  specialties: (username: string) =>
    http.get<Specialty[]>(`${BASE}/trainers/${encodeURIComponent(username)}/specialties`).then((r) => r.data),
};

// ---------------------------------------------------------------------------
// Clients — RouterRegister
// ---------------------------------------------------------------------------

export const clientsApi = {
  register: (client: ClientRegisterInput) =>
    http.post<AllClient>(`${BASE}/registerClient`, client).then((r) => r.data),

  getInfo: (username: string) =>
    http.get<ClientInfo>(`${BASE}/clients/${encodeURIComponent(username)}/allInformation`).then((r) => r.data),

  getClasses: (username: string) =>
    http.get<WorkClass[]>(`${BASE}/clients/${encodeURIComponent(username)}/allClass`).then((r) => r.data),

  updateAllInformation: (username: string, data: ClientDetailUpdateInput) =>
    http.put(`${BASE}/clients/${encodeURIComponent(username)}/changeInformation`, data).then((r) => r.data),

  updateUsername: (username: string, newUsername: string) =>
    http
      .put(`${BASE}/clients/${encodeURIComponent(username)}/${encodeURIComponent(newUsername)}/changeUsername`)
      .then((r) => r.data),

  updateEmail: (username: string, email: string) =>
    http
      .put(`${BASE}/clients/${encodeURIComponent(username)}/${encodeURIComponent(email)}/changeEmail`)
      .then((r) => r.data),

  updateMembership: (username: string, membershipType: string) =>
    http
      .put(
        `${BASE}/clients/${encodeURIComponent(username)}/${encodeURIComponent(membershipType)}/changeMembership`,
      )
      .then((r) => r.data),

  updateTrainer: (username: string, newUsernameTrainer: string) =>
    http
      .put(
        `${BASE}/clients/${encodeURIComponent(username)}/${encodeURIComponent(newUsernameTrainer)}/changeTrainer`,
      )
      .then((r) => r.data),

  // Sent as a request body, not URL segments - a password in the path would
  // end up in browser history, server access logs, and Referer headers.
  updatePassword: (username: string, oldPassword: string, newPassword: string) =>
    http
      .put(`${BASE}/clients/${encodeURIComponent(username)}/changePassword`, { oldPassword, newPassword })
      .then((r) => r.data),

  removeMembership: (username: string, membershipType: string) =>
    http
      .delete(`${BASE}/clients/${encodeURIComponent(username)}/${encodeURIComponent(membershipType)}/delete-membership`)
      .then((r) => r.data),

  removeTrainer: (username: string, trainerUsername: string) =>
    http
      .delete(`${BASE}/clients/${encodeURIComponent(username)}/${encodeURIComponent(trainerUsername)}/remove-trainer`)
      .then((r) => r.data),

  deleteAccount: (username: string, password: string) =>
    http
      .delete(`${BASE}/clients/${encodeURIComponent(username)}/deleteAccount`, { data: { password } })
      .then((r) => r.data),
};
