import type { EndpointDef } from "./types.js";

// Transcribed directly from every RouterFunction bean in server-administrator (/api/admin/**,
// routed by the gateway) and web-page (/api/page/**, routed by the gateway). server-informations
// and server-register have no gateway route and aren't included (internal-only, called by
// web-page/server-administrator themselves).
export const ENDPOINTS: EndpointDef[] = [
  // --- Admin - Clientes (ClientRouter) ---
  { method: "GET", path: "/api/admin/clients", group: "Admin - Clientes" },
  { method: "GET", path: "/api/admin/clients/{username}/allClass", group: "Admin - Clientes" },
  { method: "POST", path: "/api/admin/clients", group: "Admin - Clientes" },
  { method: "PUT", path: "/api/admin/clients/{username}", group: "Admin - Clientes" },
  { method: "DELETE", path: "/api/admin/clients/{username}/deleteAccount", group: "Admin - Clientes" },
  { method: "GET", path: "/api/admin/clients/validate/username/{username}", group: "Admin - Clientes" },
  { method: "GET", path: "/api/admin/clients/validate/email/{email}", group: "Admin - Clientes" },
  { method: "PUT", path: "/api/admin/clients/{username}/change-password/{oldPassword}/{newPassword}", group: "Admin - Clientes" },
  { method: "PUT", path: "/api/admin/clients/{username}/change-email/{email}", group: "Admin - Clientes" },
  { method: "PUT", path: "/api/admin/clients/{username}/change-username/{newUsername}", group: "Admin - Clientes" },
  { method: "PUT", path: "/api/admin/clients/{username}/update-info", group: "Admin - Clientes" },
  { method: "POST", path: "/api/admin/clients/{username}/{membershipType}/change-membership", group: "Admin - Clientes" },
  { method: "POST", path: "/api/admin/clients/{username}/{usernameTrainer}/change-trainer", group: "Admin - Clientes" },
  { method: "DELETE", path: "/api/admin/clients/{username}/{membershipType}/delete-membership", group: "Admin - Clientes" },
  { method: "DELETE", path: "/api/admin/clients/{username}/{trainerUsername}/remove-trainer", group: "Admin - Clientes" },

  // --- Admin - Equipamiento (EquipamentRouter) ---
  { method: "POST", path: "/api/admin/equipaments/create", group: "Admin - Equipamiento" },
  { method: "GET", path: "/api/admin/equipaments/all", group: "Admin - Equipamiento" },
  { method: "GET", path: "/api/admin/equipaments/{name}", group: "Admin - Equipamiento" },
  { method: "PUT", path: "/api/admin/equipaments/update/{id}", group: "Admin - Equipamiento" },
  { method: "DELETE", path: "/api/admin/equipaments/delete/{name}", group: "Admin - Equipamiento" },

  // --- Admin - Entrenadores (PreTrainerRouter) ---
  { method: "POST", path: "/api/admin/trainer", group: "Admin - Entrenadores" },
  { method: "GET", path: "/api/admin/allTrainer", group: "Admin - Entrenadores" },
  { method: "DELETE", path: "/api/admin/trainer/{username}/{password}", group: "Admin - Entrenadores" },
  { method: "GET", path: "/api/admin/trainer/{username}/allClients", group: "Admin - Entrenadores" },
  { method: "GET", path: "/api/admin/trainer/{username}/specialties", group: "Admin - Entrenadores" },
  { method: "PUT", path: "/api/admin/trainer/{username}/password/{newPassword}/{oldPassword}", group: "Admin - Entrenadores" },
  { method: "PUT", path: "/api/admin/trainer/{username}/email/{newEmail}", group: "Admin - Entrenadores" },
  { method: "PUT", path: "/api/admin/trainer/{oldUsername}/username/{newUsername}", group: "Admin - Entrenadores" },
  { method: "PUT", path: "/api/admin/trainer/{username}/BasicInformation", group: "Admin - Entrenadores" },
  { method: "POST", path: "/api/admin/trainer/{username}/addSpecialty/{newSpecialty}", group: "Admin - Entrenadores" },
  { method: "DELETE", path: "/api/admin/trainer/{username}/removeSpecialty/{specialty}", group: "Admin - Entrenadores" },

  // --- Admin - Promociones (PromotionRouter) ---
  { method: "GET", path: "/api/admin/promotions", group: "Admin - Promociones" },
  { method: "GET", path: "/api/admin/promotions/current/{currentDate}", group: "Admin - Promociones" },
  { method: "GET", path: "/api/admin/promotions/start/{date}", group: "Admin - Promociones" },
  { method: "GET", path: "/api/admin/promotions/end/{date}", group: "Admin - Promociones" },
  { method: "POST", path: "/api/admin/promotions", group: "Admin - Promociones" },
  { method: "PUT", path: "/api/admin/promotions/{id}", group: "Admin - Promociones" },
  { method: "DELETE", path: "/api/admin/promotions/{id}", group: "Admin - Promociones" },

  // --- Admin - Entrenadores de clase (RouterClassTrainer) ---
  { method: "POST", path: "/api/admin/classTrainers/create", group: "Admin - Entrenadores de clase" },
  { method: "GET", path: "/api/admin/classTrainers/all", group: "Admin - Entrenadores de clase" },
  { method: "GET", path: "/api/admin/classTrainers/{username}/specialties", group: "Admin - Entrenadores de clase" },
  { method: "PUT", path: "/api/admin/classTrainers/{username}/updateBasicInformation", group: "Admin - Entrenadores de clase" },
  { method: "GET", path: "/api/admin/classTrainers/{username}/workclasses", group: "Admin - Entrenadores de clase" },
  { method: "PUT", path: "/api/admin/classTrainers/{username}/password/{oldPassword}/{newPassword}", group: "Admin - Entrenadores de clase" },
  { method: "PUT", path: "/api/admin/classTrainers/username/{oldUsername}/{newUsername}", group: "Admin - Entrenadores de clase" },
  { method: "PUT", path: "/api/admin/classTrainers/email/{username}/{newEmail}", group: "Admin - Entrenadores de clase" },
  { method: "PUT", path: "/api/admin/classTrainers/{username}/addSpecialty/{newSpecialty}", group: "Admin - Entrenadores de clase" },
  { method: "DELETE", path: "/api/admin/classTrainers/{username}/removeSpecialty/{specialty}", group: "Admin - Entrenadores de clase" },
  { method: "PUT", path: "/api/admin/classTrainers/{username}/addClass/{newClass}", group: "Admin - Entrenadores de clase" },
  { method: "DELETE", path: "/api/admin/classTrainers/{username}/removeClass/{className}", group: "Admin - Entrenadores de clase" },
  { method: "DELETE", path: "/api/admin/classTrainers/{username}/delete/{password}", group: "Admin - Entrenadores de clase" },

  // --- Admin - Membresias (RouterMembership) ---
  { method: "GET", path: "/api/admin/memberships", group: "Admin - Membresias" },
  { method: "POST", path: "/api/admin/memberships", group: "Admin - Membresias" },
  { method: "PUT", path: "/api/admin/memberships/{id}", group: "Admin - Membresias" },
  { method: "DELETE", path: "/api/admin/memberships/{type}", group: "Admin - Membresias" },

  // --- Admin - Piscinas (RouterPool) ---
  { method: "GET", path: "/api/admin/allPools", group: "Admin - Piscinas" },
  { method: "POST", path: "/api/admin/pools/create", group: "Admin - Piscinas" },
  { method: "PUT", path: "/api/admin/pools/{name}/update", group: "Admin - Piscinas" },
  { method: "DELETE", path: "/api/admin/pools/{name}/delete", group: "Admin - Piscinas" },

  // --- Admin - Especialidades (RouterSpecialty) ---
  { method: "GET", path: "/api/admin/specialties", group: "Admin - Especialidades" },
  { method: "GET", path: "/api/admin/trainers", group: "Admin - Especialidades" },
  { method: "POST", path: "/api/admin/specialties/create", group: "Admin - Especialidades" },
  { method: "GET", path: "/api/admin/specialties/{name}", group: "Admin - Especialidades" },
  { method: "PUT", path: "/api/admin/specialties/{name}/update", group: "Admin - Especialidades" },
  { method: "DELETE", path: "/api/admin/specialties/{name}/delete", group: "Admin - Especialidades" },

  // --- Admin - Clases de trabajo (RouterWorkClass) ---
  { method: "GET", path: "/api/admin/work-classes/all", group: "Admin - Clases de trabajo" },
  { method: "GET", path: "/api/admin/work-class/{name}/schedules", group: "Admin - Clases de trabajo" },
  { method: "GET", path: "/api/admin/work-class/{name}/clients", group: "Admin - Clases de trabajo" },
  { method: "GET", path: "/api/admin/work-class/{name}/trainers", group: "Admin - Clases de trabajo" },
  { method: "POST", path: "/api/admin/work-class/create", group: "Admin - Clases de trabajo" },
  { method: "PUT", path: "/api/admin/work-class/{name}/update", group: "Admin - Clases de trabajo" },
  { method: "DELETE", path: "/api/admin/work-class/{name}/delete", group: "Admin - Clases de trabajo" },

  // --- Admin - Horarios (ScheduleRouter) ---
  { method: "POST", path: "/api/admin/schedules", group: "Admin - Horarios" },
  { method: "GET", path: "/api/admin/schedules", group: "Admin - Horarios" },
  { method: "GET", path: "/api/admin/schedules/start/{startTime}", group: "Admin - Horarios" },
  { method: "GET", path: "/api/admin/schedules/day/{day}", group: "Admin - Horarios" },
  { method: "GET", path: "/api/admin/schedules/gym/day/{day}", group: "Admin - Horarios" },
  { method: "PUT", path: "/api/admin/schedules/{id}", group: "Admin - Horarios" },
  { method: "DELETE", path: "/api/admin/schedules/{id}", group: "Admin - Horarios" },

  // --- Page - Entrenadores (preTrainers, public) ---
  { method: "GET", path: "/api/page/allTrainers", group: "Page - Entrenadores" },
  { method: "GET", path: "/api/page/trainers/{username}/specialties", group: "Page - Entrenadores" },

  // --- Page - Membresias (RouterMembership, public) ---
  { method: "GET", path: "/api/page/allMemberships", group: "Page - Membresias" },

  // --- Page - Piscinas (RouterPool, public) ---
  { method: "GET", path: "/api/page/allPools", group: "Page - Piscinas" },

  // --- Page - Promociones (RouterPromotion, public) ---
  { method: "GET", path: "/api/page/promotions/currentPromotions/{currentDate}", group: "Page - Promociones" },
  { method: "GET", path: "/api/page/promotions/specificDate/{date}", group: "Page - Promociones" },

  // --- Page - Registro y cuenta de cliente (RouterRegister) ---
  { method: "POST", path: "/api/page/registerClient", group: "Page - Registro y cuenta" },
  // Body: { "oldPassword": "...", "newPassword": "..." } - not path segments, see RegisterClientHandler.
  { method: "PUT", path: "/api/page/clients/{username}/changePassword", group: "Page - Registro y cuenta" },
  { method: "PUT", path: "/api/page/clients/{username}/{email}/changeEmail", group: "Page - Registro y cuenta" },
  { method: "PUT", path: "/api/page/clients/{username}/{membershipType}/changeMembership", group: "Page - Registro y cuenta" },
  { method: "PUT", path: "/api/page/clients/{username}/{newUsernameTrainer}/changeTrainer", group: "Page - Registro y cuenta" },
  { method: "PUT", path: "/api/page/clients/{username}/{newUsername}/changeUsername", group: "Page - Registro y cuenta" },
  { method: "PUT", path: "/api/page/clients/{username}/changeInformation", group: "Page - Registro y cuenta" },
  // Body: { "password": "..." } - not a path segment, see RegisterClientHandler.
  { method: "DELETE", path: "/api/page/clients/{username}/deleteAccount", group: "Page - Registro y cuenta" },
  { method: "DELETE", path: "/api/page/clients/{username}/{membershipType}/delete-membership", group: "Page - Registro y cuenta" },
  { method: "GET", path: "/api/page/clients/{username}/allClass", group: "Page - Registro y cuenta" },
  { method: "GET", path: "/api/page/clients/{username}/allInformation", group: "Page - Registro y cuenta" },
  { method: "DELETE", path: "/api/page/clients/{username}/{trainerUsername}/remove-trainer", group: "Page - Registro y cuenta" },

  // --- Page - Especialidades (RouterSpecialty, public) ---
  { method: "GET", path: "/api/page/allSpecialties", group: "Page - Especialidades" },

  // --- Page - Clases de trabajo (RouterWorkClass) ---
  { method: "GET", path: "/api/page/workclasses", group: "Page - Clases de trabajo" },
  { method: "GET", path: "/api/page/workclasses/{name}/schedules", group: "Page - Clases de trabajo" },
  { method: "GET", path: "/api/page/workclasses/{name}/clients", group: "Page - Clases de trabajo" },
  { method: "GET", path: "/api/page/workclasses/{name}/trainers", group: "Page - Clases de trabajo" },
];

// Public (no JWT required) per web-page's SecurityConfig - everything else needs Authorization: Bearer.
export const PUBLIC_PATHS = new Set([
  "GET /api/page/allMemberships",
  "GET /api/page/allPools",
  "GET /api/page/allSpecialties",
]);

export function isPublicPath(method: string, path: string): boolean {
  if (PUBLIC_PATHS.has(`${method} ${path}`)) return true;
  if (method === "GET" && (path.startsWith("/api/page/promotions/") || path.startsWith("/api/page/workclasses"))) return true;
  if (method === "POST" && path === "/api/page/registerClient") return true;
  return false;
}
