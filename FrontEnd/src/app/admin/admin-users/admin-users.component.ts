import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { AdminService } from '../admin.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html',
  styleUrl: './admin-users.component.css',
})
export class AdminUsersComponent implements OnInit {
  private adminService = inject(AdminService);

  DisplayedColumns: string[] = [
    'firstName',
    'lastName',
    'email',
    'contact',
    'role',
  ];
  DataSource: any;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  ngOnInit(): void {
    this.adminService.getUsers().subscribe({
      next: (data: any) => {
        this.DataSource = new MatTableDataSource(data);
        this.DataSource.paginator = this.paginator;
      },
      error: (error) => {
        console.error('Error:', error);
      },
    });
  }

  translateRole(role: string) {
    if (role === 'FIELD_OWNER') return 'Vlasnik terena';
    else return 'Sportaš';
  }
}
