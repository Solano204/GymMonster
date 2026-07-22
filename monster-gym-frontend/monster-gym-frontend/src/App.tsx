import { Routes, Route } from "react-router-dom";
import { Layout } from "@/components/Layout";
import { RequireAuth } from "@/components/RequireAuth";
import { ThemeToggle } from "@/components/ThemeToggle";
import { LoginPage } from "@/pages/LoginPage";
import { DashboardPage } from "@/pages/DashboardPage";
import { ClientsPage } from "@/pages/ClientsPage";
import { TrainersPage } from "@/pages/TrainersPage";
import { WorkClassesPage } from "@/pages/WorkClassesPage";
import { MembershipsPage } from "@/pages/MembershipsPage";
import { PoolsPage } from "@/pages/PoolsPage";
import { SpecialtiesPage } from "@/pages/SpecialtiesPage";
import { PromotionsPage } from "@/pages/PromotionsPage";

export default function App() {
  return (
    <>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<RequireAuth />}>
          <Route element={<Layout />}>
            <Route path="/" element={<DashboardPage />} />
            <Route path="/clients" element={<ClientsPage />} />
            <Route path="/trainers" element={<TrainersPage />} />
            <Route path="/workclasses" element={<WorkClassesPage />} />
            <Route path="/memberships" element={<MembershipsPage />} />
            <Route path="/pools" element={<PoolsPage />} />
            <Route path="/specialties" element={<SpecialtiesPage />} />
            <Route path="/promotions" element={<PromotionsPage />} />
          </Route>
        </Route>
      </Routes>
      <ThemeToggle />
    </>
  );
}
