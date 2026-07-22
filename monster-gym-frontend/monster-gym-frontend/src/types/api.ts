/**
 * TypeScript mirrors of the Spring Boot backend's DTOs / domain records.
 * Source: infraestrucutre.Adapters.Drivens.DTOS, domain.Entities,
 * infraestrucutre.Adapters.Drivens.Entities (java records -> TS interfaces).
 *
 * Dates: the backend serializes java.time.LocalDate as an ISO "YYYY-MM-DD" string
 * over JSON, so those fields are typed as `string` here.
 */

// ---------------------------------------------------------------------------
// Memberships
// ---------------------------------------------------------------------------

// Mirrors DtoMembershipReciving, which GET /api/page/allMemberships actually
// serializes - no `id` field exists on this DTO (unlike AllClient/etc).
export interface Membership {
  membershipType: string;
  description: string;
  hasCardio: boolean;
  hasPool: boolean;
  hasFoodCourt: boolean;
}

export interface MembershipCreateInput {
  membershipType: string;
  description: string;
  hasCardio: boolean;
  hasPool: boolean;
  hasFoodCourt: boolean;
}

// ---------------------------------------------------------------------------
// Pools
// ---------------------------------------------------------------------------

// Mirrors DtoPoolReciving - no dateClean/startDate/endDate on the wire,
// despite the maintenance-window fields the earlier UI assumed existed.
export interface Pool {
  id: number;
  name: string;
  description: string;
}

export interface PoolCreateInput {
  id?: number;
  name: string;
  description: string;
}

// ---------------------------------------------------------------------------
// Specialties
// ---------------------------------------------------------------------------

// Mirrors DtoSpecialtyRecived - no `id` field on the wire.
export interface Specialty {
  name: string;
  description: string;
}

export interface SpecialtyCreateInput {
  name: string;
  description: string;
}

// ---------------------------------------------------------------------------
// Work classes / schedules
// ---------------------------------------------------------------------------

// Mirrors infraestrucutre.Adapters.Drivens.Entities.WorkClass - day/startTime/endTime
// live on Schedule instead (GET .../schedules), not on the work class itself.
export interface WorkClass {
  name: string;
  description: string;
  duration: string;
}

export interface Schedule {
  day: string;
  startTime: string;
  endTime: string;
}

// ---------------------------------------------------------------------------
// Trainers
// ---------------------------------------------------------------------------

export interface TrainerListItem {
  username: string;
  email: string;
  name: string;
  secondName: string;
  lastNameP: string;
  lastNameM: string;
  age: string;
  height: string;
  weight: string;
}

// ---------------------------------------------------------------------------
// Promotions
// ---------------------------------------------------------------------------

export interface Promotion {
  description: string;
  duration: string;
  percentageDiscount: number;
  startDate: string;
  endDate: string;
  active: boolean;
}

// ---------------------------------------------------------------------------
// Clients / members
// ---------------------------------------------------------------------------

/** Mirrors infraestrucutre.Adapters.Drivens.Entities.AllClient — request body for
 * registration and the shape returned by most client mutation endpoints. */
export interface AllClient {
  id: number | null;
  username: string;
  password: string;
  email: string;
  trainername: string | null;
  name: string;
  secondname: string;
  lastnamep: string;
  lastnamem: string;
  age: string;
  height: string;
  weight: string;
  membershiptype: string;
  startdate: string | null;
  startinscription: string | null;
  endinscription: string | null;
  price: number | null;
}

export type ClientRegisterInput = Omit<AllClient, "id" | "startdate" | "startinscription" | "endinscription" | "price"> &
  Partial<Pick<AllClient, "startdate" | "startinscription" | "endinscription" | "price">>;

/**
 * GET /clients/{username}/allInformation - the handler serializes this route's
 * response as the raw AllClient record (RegisterClientHandler.getClient), not a
 * dedicated read DTO, so the field names below intentionally match AllClient's
 * (lowercase compound names, not camelCase) rather than the other endpoints'
 * camelCase DTOs. The response also includes `password` (a hash) and the
 * inscription/date fields, which aren't declared here since nothing in the UI
 * should render them - backend really should have a slimmer read DTO for this
 * route instead of reusing the write-side entity.
 */
export interface ClientInfo {
  id: number;
  username: string;
  email: string;
  name: string;
  secondname: string;
  lastnamep: string;
  lastnamem: string;
  age: string;
  height: string;
  weight: string;
  membershiptype: string;
  trainername: string | null;
}

/** Body for PUT /clients/{username}/changeInformation (mirrors DtoDetailUserSent) */
export interface ClientDetailUpdateInput {
  name: string;
  secondName: string;
  lastNameM: string;
  lastNameP: string;
  age: string;
  weight: string;
  height: string;
}

/** Paginated roster entries returned for work class clients/trainers. */
export interface ClientRosterEntry {
  name: string;
  secondName: string;
  lastNameM: string;
  lastNameP: string;
  age: string;
}

// ---------------------------------------------------------------------------
// Generic API error shape produced by the reactive error handlers
// ---------------------------------------------------------------------------

export interface ApiErrorBody {
  error: string;
  timestamp: string;
  status?: number;
}
