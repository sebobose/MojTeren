import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { AdminService } from '../admin.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';

@Component({
  selector: 'app-admin-sports',
  templateUrl: './admin-sports.component.html',
  styleUrl: './admin-sports.component.css',
})
export class AdminSportsComponent implements OnInit {
  private adminService = inject(AdminService);

  displayedColumns: string[] = [
    'sportName',
    'fields',
    'reservations',
    'delete',
  ];
  dataSource: MatTableDataSource<SportsData> = new MatTableDataSource();
  disabledButtons: Map<string, boolean> = new Map<string, boolean>();
  addSportError: boolean = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('sportArea') sportArea!: any;
  @ViewChild(MatTable) table!: MatTable<any>;

  ngOnInit(): void {
    this.dataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case 'sportName':
          return item.sportName;
        case 'fields':
          return item.fields;
        case 'reservations':
          return item.reservations;
        default:
          return item.sportName;
      }
    };
    this.adminService.getAdminSports().subscribe({
      next: (data: any) => {
        this.dataSource = new MatTableDataSource<SportsData>(data);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        for (let element of this.dataSource.data) {
          this.disabledButtons.set(element.sportName, element.fields > 0);
        }
      },
      error: (error) => {
        console.error('Error:', error);
      },
    });
  }

  deleteSport(element: any) {
    this.adminService.deleteSport(element.sportName).subscribe({
      next: (data: any) => {
        location.reload();
      },
      error: (error) => {
        console.error('Error:', error);
      },
    });
  }

  addSport() {
    let sportName = this.sportArea.nativeElement.value;
    if (sportName === '') {
      return;
    } else if (this.dataSource.data.find((x) => x.sportName === sportName)) {
      console.log('Sport already exists');
      this.addSportError = true;
      return;
    } else {
      this.disabledButtons.set(sportName, true);
      this.adminService.addSport(sportName).subscribe({
        next: (data: any) => {
          location.reload();
        },
        error: (error) => {
          console.error('Error:', error);
        },
      });
    }
  }
}

export interface SportsData {
  sportName: string;
  fields: number;
  reservations: number;
}
