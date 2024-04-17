import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { AdminService } from '../admin.service';

@Component({
  selector: 'app-admin-sport-centers',
  templateUrl: './admin-sport-centers.component.html',
  styleUrl: './admin-sport-centers.component.css',
})
export class AdminSportCentersComponent implements OnInit {
  private router = inject(Router);
  private adminService = inject(AdminService);

  @ViewChild('searchArea') searchArea!: any;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<any>;

  displayedColumns: string[] = [
    'sportCenterName',
    'owner',
    'address',
    // 'details',
    'edit',
  ];
  dataSource: MatTableDataSource<SportCenterData> = new MatTableDataSource();

  ngOnInit(): void {
    this.dataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case 'sportCenterName':
          return item.sportCenterName;
        case 'owner':
          return item.owner;
        default:
          return item.sportCenterName;
      }
    };
    this.adminService.getAdminSportCenters().subscribe({
      next: (data: any) => {
        this.dataSource = new MatTableDataSource<SportCenterData>(data);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (error) => {
        console.error('Error:', error);
      },
    });
  }

  addSportCenter() {
    this.router.navigate(['/add-sport-center']).then(() => {
      window.location.reload();
    });
  }

  doSearch() {
    const searchValue = this.searchArea.nativeElement.value;
    this.dataSource.filter = searchValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  // sportCenterDetails(element: any) {}

  editSportCenter(element: any) {
    this.router
      .navigate(['/edit-sport-center/' + element.sportCenterId])
      .then(() => {
        window.location.reload();
      });
  }
}

export interface SportCenterData {
  sportCenterId: number;
  sportCenterName: string;
  owner: string;
  cityName: string;
  zipCode: string;
  streetAndNumber: string;
}
