import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  clientsApi,
  membershipsApi,
  poolsApi,
  promotionsApi,
  specialtiesApi,
  trainersApi,
  workClassesApi,
  type Page,
} from "@/api/endpoints";
import { ApiError } from "@/lib/http";
import type { ClientDetailUpdateInput, ClientRegisterInput } from "@/types/api";

// There's no dedicated "does this username exist" endpoint, only
// GET .../allInformation (404s if not found) - reused here as the existence
// check. A username collision would otherwise either 500 or silently
// overwrite depending on how the backend's registerClient handles it, and
// the resulting error wasn't user-legible either way.
async function assertUsernameAvailable(username: string) {
  try {
    await clientsApi.getInfo(username);
  } catch (err) {
    if (err instanceof ApiError && err.status === 404) return; // available
    throw err; // some other failure (network, 5xx) - surface it as-is
  }
  throw new ApiError(`Username "${username}" is already taken`, 409);
}

// ---------------------------------------------------------------------------
// Read hooks
// ---------------------------------------------------------------------------

export const useMemberships = () =>
  useQuery({ queryKey: ["memberships"], queryFn: membershipsApi.list });

export const usePools = () => useQuery({ queryKey: ["pools"], queryFn: poolsApi.list });

export const useSpecialties = () =>
  useQuery({ queryKey: ["specialties"], queryFn: specialtiesApi.list });

export const useCurrentPromotions = (currentDate: string) =>
  useQuery({
    queryKey: ["promotions", "current", currentDate],
    queryFn: () => promotionsApi.current(currentDate),
    enabled: Boolean(currentDate),
  });

export const useWorkClasses = () =>
  useQuery({ queryKey: ["workClasses"], queryFn: workClassesApi.list });

export const useWorkClassSchedules = (name: string | undefined) =>
  useQuery({
    queryKey: ["workClasses", name, "schedules"],
    queryFn: () => workClassesApi.schedules(name as string),
    enabled: Boolean(name),
  });

export const useWorkClassClients = (name: string | undefined, page: Page = {}) =>
  useQuery({
    queryKey: ["workClasses", name, "clients", page],
    queryFn: () => workClassesApi.clients(name as string, page),
    enabled: Boolean(name),
  });

export const useWorkClassTrainers = (name: string | undefined, page: Page = {}) =>
  useQuery({
    queryKey: ["workClasses", name, "trainers", page],
    queryFn: () => workClassesApi.trainers(name as string, page),
    enabled: Boolean(name),
  });

export const useTrainers = (page: Page = {}) =>
  useQuery({ queryKey: ["trainers", page], queryFn: () => trainersApi.list(page) });

export const useTrainerSpecialties = (username: string | undefined) =>
  useQuery({
    queryKey: ["trainers", username, "specialties"],
    queryFn: () => trainersApi.specialties(username as string),
    enabled: Boolean(username),
  });

export const useClientInfo = (username: string | undefined) =>
  useQuery({
    queryKey: ["clients", username, "info"],
    queryFn: () => clientsApi.getInfo(username as string),
    enabled: Boolean(username),
    retry: false,
  });

export const useClientClasses = (username: string | undefined) =>
  useQuery({
    queryKey: ["clients", username, "classes"],
    queryFn: () => clientsApi.getClasses(username as string),
    enabled: Boolean(username),
  });

// ---------------------------------------------------------------------------
// Mutation hooks
// ---------------------------------------------------------------------------

export const useRegisterClient = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (input: ClientRegisterInput) => {
      await assertUsernameAvailable(input.username);
      return clientsApi.register(input);
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ["clients"] }),
  });
};

export const useUpdateClientInfo = (username: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: ClientDetailUpdateInput) => clientsApi.updateAllInformation(username, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["clients", username] }),
  });
};

export const useUpdateClientMembership = (username: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (membershipType: string) => clientsApi.updateMembership(username, membershipType),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["clients", username] }),
  });
};

export const useUpdateClientTrainer = (username: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (newUsernameTrainer: string) => clientsApi.updateTrainer(username, newUsernameTrainer),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["clients", username] }),
  });
};

export const useRemoveClientTrainer = (username: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (trainerUsername: string) => clientsApi.removeTrainer(username, trainerUsername),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["clients", username] }),
  });
};

export const useRemoveClientMembership = (username: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (membershipType: string) => clientsApi.removeMembership(username, membershipType),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["clients", username] }),
  });
};

export const useUpdateClientUsername = (username: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (newUsername: string) => clientsApi.updateUsername(username, newUsername),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["clients"] }),
  });
};

export const useUpdateClientEmail = (username: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (email: string) => clientsApi.updateEmail(username, email),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["clients", username] }),
  });
};

export const useUpdateClientPassword = (username: string) => {
  return useMutation({
    mutationFn: ({ oldPassword, newPassword }: { oldPassword: string; newPassword: string }) =>
      clientsApi.updatePassword(username, oldPassword, newPassword),
  });
};

export const useDeleteClientAccount = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ username, password }: { username: string; password: string }) =>
      clientsApi.deleteAccount(username, password),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["clients"] }),
  });
};
