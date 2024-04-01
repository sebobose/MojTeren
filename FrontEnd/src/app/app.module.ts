import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { HeaderComponent } from './header/header.component';
import { MatIconModule } from '@angular/material/icon';
import { HomepageComponent } from './homepage/homepage.component';
import { MatSliderModule } from '@angular/material/slider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
  MatNativeDateModule,
  MAT_DATE_FORMATS,
  MAT_DATE_LOCALE,
  MatOption,
} from '@angular/material/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AdminRequestsComponent } from './admin/admin-requests/admin-requests.component';
import { AdminSportCentersComponent } from './admin/admin-sport-centers/admin-sport-centers.component';
import { AdminUsersComponent } from './admin/admin-users/admin-users.component';
import { AdminSportsComponent } from './admin/admin-sports/admin-sports.component';
import { MatTab, MatTabGroup } from '@angular/material/tabs';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
} from '@angular/material/table';
import { MatPaginator, MatPaginatorIntl } from '@angular/material/paginator';
import { CustomPaginatorIntl } from './custom-angular-material/custom-paginator-intl';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { AddSportCenterComponent } from './sport-center/add-sport-center/add-sport-center.component';
import { EditSportCenterComponent } from './sport-center/edit-sport-center/edit-sport-center.component';
import { ConfirmDialogComponent } from './sport-center/edit-sport-center/confirm-dialog.component';
import { MatButton } from '@angular/material/button';
import { AddFieldComponent } from './field/add-field/add-field.component';
import { MatSelect } from '@angular/material/select';
import { MatCheckbox } from '@angular/material/checkbox';

export const MY_DATE_FORMATS = {
  parse: {
    dateInput: 'DD/MM/YYYY',
  },
  display: {
    dateInput: 'DD/MM/YYYY',
  },
};

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HeaderComponent,
    HomepageComponent,
    AdminRequestsComponent,
    AdminSportCentersComponent,
    AdminUsersComponent,
    AdminSportsComponent,
    AddSportCenterComponent,
    EditSportCenterComponent,
    ConfirmDialogComponent,
    AddFieldComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    RouterOutlet,
    ReactiveFormsModule,
    RouterLink,
    AppRoutingModule,
    MatIconModule,
    MatSliderModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    BrowserAnimationsModule,
    MatTabGroup,
    MatTab,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderRowDef,
    MatRowDef,
    MatHeaderRow,
    MatRow,
    MatPaginator,
    MatSort,
    MatSortHeader,
    MatSelect,
    MatOption,
    MatCheckbox,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'hr-HR' },
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS },
    { provide: MatPaginatorIntl, useClass: CustomPaginatorIntl },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
