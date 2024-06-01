import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { AdminService } from '../admin.service';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { ScDialogComponent } from '../../sport-center/sc-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-admin-requests',
  templateUrl: './admin-requests.component.html',
  styleUrl: './admin-requests.component.css',
})
export class AdminRequestsComponent implements OnInit {
  private adminService = inject(AdminService);
  private dialog = inject(MatDialog);

  @ViewChild('searchArea') searchArea!: any;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<any>;

  displayedColumns: string[] = ['sportCenterName', 'address', 'owner', 'edit'];
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
    this.adminService.getSportCenterRequests().subscribe({
      next: (data: any) => {
        console.log(data);
        this.dataSource = new MatTableDataSource<SportCenterData>(data);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (error) => {
        console.error('Error:', error);
      },
    });
  }

  doSearch() {
    const searchValue = this.searchArea.nativeElement.value;
    this.dataSource.filter = searchValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  showSportCenterDetails(element: any) {
    const dialogRef = this.dialog.open(ScDialogComponent, {
      height: '670px',
      width: '700px',
      data: element,
    });
    dialogRef.afterClosed().subscribe((reason: any) => {
      if (reason) {
        console.log(reason);
        this.adminService
          .resolveRequest(element.sportCenterId, reason)
          .subscribe({
            next: () => {
              window.location.reload();
            },
          });
      }
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
  images: any;
}
