import { RouterModule, Routes } from '@angular/router';
import { AuthLoginGuard } from './auth/auth.loginguard';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { NgModule } from '@angular/core';
import { HomepageComponent } from './homepage/homepage.component';
import { AuthGuard } from './auth/auth.guard';
import { AdminRequestsComponent } from './admin-requests/admin-requests.component';
import { AdminFieldsComponent } from './admin-fields/admin-fields.component';
import { AdminUsersComponent } from './admin-users/admin-users.component';
import { AdminSportsComponent } from './admin-sports/admin-sports.component';

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
    path: 'admin/fields',
    component: AdminFieldsComponent,
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
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
