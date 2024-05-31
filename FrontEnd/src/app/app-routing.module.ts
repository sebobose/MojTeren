import { RouterModule, Routes } from '@angular/router';
import { AuthLoginGuard } from './auth/auth.loginguard';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { NgModule } from '@angular/core';
import { HomepageComponent } from './homepage/homepage.component';
import { AuthGuard } from './auth/auth.guard';
import { AdminRequestsComponent } from './admin/admin-requests/admin-requests.component';
import { AdminSportCentersComponent } from './admin/admin-sport-centers/admin-sport-centers.component';
import { AdminUsersComponent } from './admin/admin-users/admin-users.component';
import { AdminSportsComponent } from './admin/admin-sports/admin-sports.component';
import { AddSportCenterComponent } from './sport-center/add-sport-center/add-sport-center.component';
import { EditSportCenterComponent } from './sport-center/edit-sport-center/edit-sport-center.component';
import { FieldReservationsComponent } from './reservations/field-reservations/field-reservations.component';
import { UserReservationsComponent } from './reservations/user-reservations/user-reservations.component';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'login', component: LoginComponent, canActivate: [AuthLoginGuard] },
  {
    path: 'register',
    component: RegisterComponent,
    canActivate: [AuthLoginGuard],
  },
  { path: 'home', component: HomepageComponent },
  {
    path: 'admin/requests',
    component: AdminRequestsComponent,
    canActivate: [AuthGuard],
    data: { requiredRole: 'ADMIN' },
  },
  {
    path: 'admin/sport-centers',
    component: AdminSportCentersComponent,
    canActivate: [AuthGuard],
    data: { requiredRole: 'ADMIN' },
  },
  {
    path: 'admin/users',
    component: AdminUsersComponent,
    canActivate: [AuthGuard],
    data: { requiredRole: 'ADMIN' },
  },
  {
    path: 'admin/sports',
    component: AdminSportsComponent,
    canActivate: [AuthGuard],
    data: { requiredRole: 'ADMIN' },
  },
  {
    path: 'field-owner/home',
    component: HomepageComponent,
    canActivate: [AuthGuard],
    data: { requiredRole: 'FIELD_OWNER' },
  },
  {
    path: 'add-sport-center',
    component: AddSportCenterComponent,
    canActivate: [AuthGuard],
    data: { requiredRole: ['FIELD_OWNER', 'ADMIN'] },
  },
  {
    path: 'edit-sport-center/:id',
    component: EditSportCenterComponent,
    canActivate: [AuthGuard],
    data: { requiredRole: ['FIELD_OWNER', 'ADMIN'] },
  },
  { path: 'reservations/:sport/:id', component: FieldReservationsComponent },
  {
    path: 'user-reservations',
    component: UserReservationsComponent,
    canActivate: [AuthGuard],
    data: { requiredRole: 'ATHLETE' },
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
