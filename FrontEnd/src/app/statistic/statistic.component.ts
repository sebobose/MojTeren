import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { StatisticService } from './statistic.service';

@Component({
  selector: 'app-statistic',
  templateUrl: './statistic.component.html',
  styleUrls: ['./statistic.component.css'],
})
export class StatisticComponent implements OnInit {
  private formBuilder = inject(FormBuilder);
  private statisticsService = inject(StatisticService);

  filterForm: any = this.formBuilder.group({
    sportCenters: [['Svi'], Validators.required],
    fields: [['Svi'], Validators.required],
    period: ['Bilo kad', Validators.required],
    date: [''],
  });
  sportCenters: any = ['Svi'];
  fields: any = ['Svi'];
  periods: any = ['Bilo kad', 'Mjesec', 'Tjedan', 'Dan'];
  data: any;
  dateError: boolean = false;
  statistics: any = [];
  public periodsData = {
    labels: [
      'Jutro (6:00-10:00)',
      'Podne (10:00-15:00)',
      'Poslijepodne (15:00-19:00)',
      'Večer (19:00-24:00)',
    ],
    datasets: [
      {
        label: 'Broj rezervacija po vremenskom periodu',
        data: [0, 0, 0, 0],
        backgroundColor: ['#e3bd2a'],
        borderColor: ['#e3bd2a'],
        borderWidth: 1,
      },
    ],
  };
  public lengthData = {
    labels: ['< 1h', '1-2h', '> 2h'],
    datasets: [
      {
        label: 'Broj rezervacija po trajanju',
        data: [0, 0, 0],
        backgroundColor: ['#e3bd2a'],
        borderColor: ['#e3bd2a'],
        borderWidth: 1,
      },
    ],
  };
  public histogramOptions = {
    scales: {
      y: {
        beginAtZero: true,
        grid: {
          color: '#646669',
        },
      },
      x: {
        grid: {
          color: '#646669',
        },
      },
    },
  };

  ngOnInit() {
    this.statisticsService.getSportCentersAndFields().subscribe((data: any) => {
      this.data = data;
      for (let sportCenter of data) {
        this.sportCenters.push(sportCenter.sportCenter);
      }
    });
  }

  applyFilters() {
    if (this.filterForm.value.period == 'Bilo kad') {
      this.filterForm.controls.date.setValue('');
    } else if (!this.filterForm.value.date) {
      this.dateError = true;
      return;
    }
    if (!this.filterForm.valid) {
      console.log('Invalid form');
      return;
    }
    this.statisticsService.getStatistics(this.filterForm.value).subscribe({
      next: (data: any) => {
        if (this.filterForm.invalid) {
          return;
        }
        console.log(data);
        this.statistics = [];

        for (let [length, count] of Object.entries(data.reservationsByLength)) {
          // @ts-ignore
          this.lengthData.datasets[0].data[length] = count;
        }
        for (let [hour, count] of Object.entries(data.reservationsByHour)) {
          // @ts-ignore
          this.periodsData.datasets[0].data[hour] = count;
        }

        if (
          this.filterForm.value.sportCenters.includes('Svi') ||
          this.filterForm.value.sportCenters.length > 1
        ) {
          this.statistics.push(['Najpopularniji centar', data.chosenField]);
          this.statistics.push(['Najpopularniji teren', data.bestField]);
          this.statistics.push(['Najpopularniji sport', data.sport]);
          this.statistics.push([
            'Datum kreiranja računa',
            data.creationDate.split('-').reverse().join('.'),
          ]);
        } else if (
          this.filterForm.value.fields.includes('Svi') ||
          this.filterForm.value.fields.length > 1
        ) {
          this.statistics.push(['Sportski centar', data.chosenField]);
          this.statistics.push(['Najpopularniji teren', data.bestField]);
          this.statistics.push(['Najpopularniji sport', data.sport]);
          this.statistics.push([
            'Datum kreiranja sportskog centra',
            data.creationDate.split('-').reverse().join('.'),
          ]);
        } else {
          this.statistics.push(['Teren', data.chosenField]);
          this.statistics.push(['Sport', data.sport]);
          this.statistics.push([
            'Datum kreiranja terena',
            data.creationDate.split('-').reverse().join('.'),
          ]);
        }
        this.statistics.push(['Aktivne rezervacije', data.activeReservations]);
        this.statistics.push([
          'Otkazane rezervacija',
          data.canceledReservations,
        ]);
        this.statistics.push([
          'Završene rezervacije',
          data.finishedReservations,
        ]);
        this.statistics.push([
          'Prosječno trajanje rezervacije',
          this.convertMinutesToTime(data.averageReservationTime) + ' h',
        ]);
        this.statistics.push([
          'Zarada',
          parseFloat(data.income.toFixed(2)) + ' €',
        ]);
      },
      error: (error: any) => {
        console.log(error);
      },
    });
  }

  convertMinutesToTime(minutes: number): string {
    let hours = Math.floor(minutes / 60);
    let mins = Math.ceil(minutes % 60);
    return `${hours}:${mins < 10 ? '0' : ''}${mins}`;
  }

  getFields() {
    if (!this.filterForm.value.sportCenters) {
      return;
    }
    if (
      this.filterForm.value.sportCenters.includes('Svi') &&
      this.filterForm.value.sportCenters.length > 1
    ) {
      this.filterForm.controls.sportCenters.setValue(['Svi']);
      this.filterForm.controls.fields.setValue(['Svi']);
      this.fields = ['Svi'];
      return;
    } else if (this.filterForm.value.sportCenters.length > 1) {
      this.filterForm.controls.fields.setValue(['Svi']);
      this.fields = ['Svi'];
    } else if (this.filterForm.value.sportCenters.length == 1) {
      this.filterForm.controls.fields.setValue(['Svi']);
      for (let sportcenter of this.data) {
        if (sportcenter.sportCenter == this.filterForm.value.sportCenters[0]) {
          for (let field of sportcenter.fields) {
            this.fields.push(field);
          }
        }
      }
    }
  }

  fieldChanged() {
    if (!this.filterForm.value.fields) {
      return;
    }
    if (
      this.filterForm.value.fields.includes('Svi') ||
      this.filterForm.value.fields.length > 1
    ) {
      this.filterForm.controls.fields.setValue(['Svi']);
      return;
    }
  }

  dateChanged() {
    if (this.filterForm.value.period == 'Bilo kad') {
      this.filterForm.controls.date.setValue('');
    }
    this.dateError = false;
  }
}
