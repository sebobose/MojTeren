import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { SportCenterService } from '../sport-center.service';

@Component({
  selector: 'app-sport-centers-list',
  templateUrl: './sport-centers-list.component.html',
  styleUrl: './sport-centers-list.component.css',
})
export class SportCentersListComponent implements OnInit {
  private router = inject(Router);
  private sportCenterService = inject(SportCenterService);

  @ViewChild('searchArea') searchArea!: any;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<any>;

  displayedColumns: string[] = ['sportCenterName', 'address', 'edit'];
  dataSource: MatTableDataSource<SportCenterData> = new MatTableDataSource();
  disabledButtons: Map<number, boolean> = new Map<number, boolean>();
  role: string = '';
  disabled = true;
  statusMap: any = {
    ACTIVE: 'Aktivan',
    REJECTED: 'Odbijen',
    PENDING: 'U obradi',
  };

  ngOnInit(): void {
    this.role = localStorage.getItem('role') || '';
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
    this.sportCenterService.getSportCenters().subscribe({
      next: (data: any) => {
        this.dataSource = new MatTableDataSource<SportCenterData>(data);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        if (this.role === 'ADMIN') {
          this.displayedColumns.splice(2, 0, 'owner');
        } else if (this.role === 'FIELD_OWNER') {
          this.displayedColumns.splice(2, 0, 'status');
          this.displayedColumns.splice(3, 0, 'statusReason');
        }
        for (let element of this.dataSource.data) {
          element.status = this.statusMap[element.status];
          this.disabledButtons.set(
            element.sportCenterId,
            element.status != 'Aktivan' || element.sport == '',
          );
        }
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

  editSportCenter(element: any) {
    this.router
      .navigate(['/edit-sport-center/' + element.sportCenterId])
      .then(() => {
        window.location.reload();
      });
  }

  sportCenterReservations(element: any) {
    this.router
      .navigate([
        '/reservations/' + element.sport + '/' + element.sportCenterId,
      ])
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
  status: string;
  statusReason: string;
  sport: string;
}
