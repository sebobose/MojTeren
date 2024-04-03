import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { FormArray, FormBuilder, Validators } from '@angular/forms';
import { FieldService } from '../field.service';
import { ConfirmDialogComponent } from '../../sport-center/edit-sport-center/confirm-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-add-field',
  templateUrl: './add-field.component.html',
  styleUrl: './add-field.component.css',
})
export class AddFieldComponent implements OnInit {
  private fieldService = inject(FieldService);
  private formBuilder = inject(FormBuilder);
  private dialog = inject(MatDialog);

  @Input() sportCenterId!: any;
  @Input() fieldId!: any;
  @Output() closeFormEvent = new EventEmitter<string>();

  protected role = localStorage.getItem('role');
  url: any = [];
  sports: any;
  sportSelected: boolean = false;
  resTimeSelected: boolean = false;
  timeSlotSelected: boolean = false;
  fieldAvailability = new Array(7).fill(0);
  workHoursPattern =
    '^([0-1]?[0-9]|2[0-3]):[0-5][0-9]-([0-1]?[0-9]|2[0-3]):[0-5][0-9]$';
  minResTimes = [
    '30 min',
    '45 min',
    '1 h',
    '1 h 15 min',
    '1 h 30 min',
    '1 h 45 min',
    '2 h',
  ];
  timeSlots = [
    '5 min',
    '10 min',
    '15 min',
    '20 min',
    '25 min',
    '30 min',
    '45 min',
    '1 h',
  ];
  days: { name: string; value: string }[] = [
    {
      name: 'Monday',
      value: 'Ponedjeljak',
    },
    {
      name: 'Tuesday',
      value: 'Utorak',
    },
    {
      name: 'Wednesday',
      value: 'Srijeda',
    },
    {
      name: 'Thursday',
      value: 'Četvrtak',
    },
    {
      name: 'Friday',
      value: 'Petak',
    },
    {
      name: 'Saturday',
      value: 'Subota',
    },
    {
      name: 'Sunday',
      value: 'Nedjelja',
    },
  ];

  addForm = this.formBuilder.nonNullable.group(
    {
      fieldName: ['', [Validators.required]],
      sport: ['', [Validators.required]],
      minResTime: ['', [Validators.required]],
      timeSlot: ['', [Validators.required]],
      description: ['', [Validators.required]],
      Monday: ['', [Validators.pattern(this.workHoursPattern)]],
      Tuesday: ['', [Validators.pattern(this.workHoursPattern)]],
      Wednesday: ['', [Validators.pattern(this.workHoursPattern)]],
      Thursday: ['', [Validators.pattern(this.workHoursPattern)]],
      Friday: ['', [Validators.pattern(this.workHoursPattern)]],
      Saturday: ['', [Validators.pattern(this.workHoursPattern)]],
      Sunday: ['', [Validators.pattern(this.workHoursPattern)]],
      images: this.formBuilder.array([]),
    },
    {
      updateOn: 'blur',
    },
  );

  ngOnInit() {
    this.fieldService.getSports().subscribe({
      next: (data) => {
        this.sports = data;
      },
      error: (error) => {
        console.error('Error:', error);
      },
    });
    if (this.fieldId) {
      this.fieldService.getFieldById(this.fieldId).subscribe({
        next: (data: any) => {
          this.addForm.controls.fieldName.setValue(data.fieldName);
          this.addForm.controls.sport.setValue(data.sport);
          this.sportSelected = true;
          if (data.minResTime / 60 >= 1) {
            let hours = Math.floor(data.minResTime / 60);
            let minutes =
              data.minResTime % 60 ? ' ' + (data.minResTime % 60) + ' min' : '';
            this.addForm.controls.minResTime.setValue(hours + ' h' + minutes);
          } else {
            this.addForm.controls.minResTime.setValue(data.minResTime + ' min');
          }
          this.resTimeSelected = true;
          if (data.timeSlot / 60 >= 1) {
            let hours = Math.floor(data.timeSlot / 60);
            let minutes =
              data.timeSlot % 60 ? ' ' + (data.timeSlot % 60) + ' min' : '';
            this.addForm.controls.timeSlot.setValue(hours + ' h' + minutes);
          } else {
            this.addForm.controls.timeSlot.setValue(data.timeSlot + ' min');
          }
          this.timeSlotSelected = true;
          this.addForm.controls.description.setValue(data.description);
          for (let i of data.fieldAvailabilities) {
            this.fieldAvailability[
              this.days.findIndex((x) => x.name === i.dayOfWeek)
            ] = 1;
            (this.addForm.controls as { [key: string]: any })[
              i.dayOfWeek
            ].setValue(i.startTime + '-' + i.endTime);
          }
          for (let i of data.images) {
            this.url.push('data:image/png;base64,' + i);
          }
          const images = this.addForm.get('images') as FormArray;
          for (let i of data.images) {
            images.push(
              this.formBuilder.control(
                this.base64toFile(
                  'data:image/png;base64,' + i,
                  'picture.jpg',
                  'image/jpeg',
                ),
              ),
            );
          }
        },
        error: (error) => {
          console.error('Error:', error);
        },
      });
    }
  }

  addField() {
    if (this.addForm.valid && this.fieldAvailability.includes(1)) {
      this.addForm.markAllAsTouched();
      for (let i = 0; i < this.days.length; i++) {
        if (
          this.fieldAvailability[i] &&
          this.checkWorkHours(this.days[i].name)
        ) {
          console.log('Form is invalid');
          console.log(this.addForm);
          return;
        }
      }
      const formData = new FormData();
      formData.append('fieldName', this.addForm.controls.fieldName.value);
      formData.append('sportName', this.addForm.controls.sport.value);
      formData.append(
        'minResTime',
        this.convertTimeToMinutes(
          this.addForm.controls.minResTime.value,
        ).toString(),
      );
      formData.append(
        'timeSlot',
        this.convertTimeToMinutes(
          this.addForm.controls.timeSlot.value,
        ).toString(),
      );
      formData.append('description', this.addForm.controls.description.value);
      formData.append('sportCenterId', this.sportCenterId);
      const images = this.addForm.get('images') as FormArray;
      for (let i = 0; i < this.addForm.controls.images.value.length; i++) {
        formData.append('images', images.at(i).value);
      }
      let fieldAvailabilities = [];
      for (let i = 0; i < this.days.length; i++) {
        if (this.fieldAvailability[i]) {
          let workHours = (this.addForm.controls as { [key: string]: any })[
            this.days[i].name
          ];
          let fieldAvailability = {
            dayOfWeek: this.days[i].name,
            startTime: workHours.value.split('-')[0],
            endTime: workHours.value.split('-')[1],
          };
          fieldAvailabilities.push(fieldAvailability);
        }
      }
      for (let i = 0; i < fieldAvailabilities.length; i++) {
        formData.append(
          `fieldAvailabilities[${i}].dayOfWeek`,
          fieldAvailabilities[i].dayOfWeek,
        );
        formData.append(
          `fieldAvailabilities[${i}].startTime`,
          fieldAvailabilities[i].startTime,
        );
        formData.append(
          `fieldAvailabilities[${i}].endTime`,
          fieldAvailabilities[i].endTime,
        );
      }
      if (this.fieldId) {
        this.fieldService.editField(formData, this.fieldId).subscribe({
          next: () => {
            location.reload();
          },
          error: (error) => {
            console.error('Error:', error);
          },
        });
      } else {
        this.fieldService.addField(formData).subscribe({
          next: () => {
            location.reload();
          },
          error: (error) => {
            console.error('Error:', error);
          },
        });
      }
    } else {
      console.log('Form is invalid');
      console.log(this.addForm);
    }
  }

  handleFileInput(event: any): void {
    let reader = new FileReader();
    reader.readAsDataURL(event.target.files[0]);
    const images = this.addForm.get('images') as FormArray;
    images.push(this.formBuilder.control(event.target.files[0]));
    reader.onload = (_event) => {
      this.url.push(reader.result);
    };
    event.target.value = null;
  }

  onDeleteImg(id: number) {
    this.url.splice(id, 1);
    const images = this.addForm.get('images') as FormArray;
    images.removeAt(id);
  }

  checkSportSelected(event: any) {
    this.sportSelected = event !== '';
  }

  checkResTimeSelected(event: any) {
    this.resTimeSelected = event !== '';
  }

  checkTimeSlotSelected(event: any) {
    this.timeSlotSelected = event !== '';
  }

  dayChecked(index: any) {
    this.fieldAvailability[index] = this.fieldAvailability[index] === 0 ? 1 : 0;
    if (!this.fieldAvailability[index]) {
      (this.addForm.controls as { [key: string]: any })[
        this.days[index].name
      ].setValue('');
    }
  }

  checkWorkHours(name: string) {
    let workHours = (this.addForm.controls as { [key: string]: any })[name];
    if (!workHours.invalid && workHours.value !== '' && workHours.touched) {
      let time1 = workHours.value.split('-')[0];
      let time2 = workHours.value.split('-')[1];
      if (time1.split(':')[0].length === 1) {
        time1 = '0' + time1;
      }
      if (time2.split(':')[0].length === 1) {
        time2 = '0' + time2;
      }
      let date1 = new Date(`1970-01-01T${time1}:00`);
      let date2 = new Date(`1970-01-01T${time2}:00`);
      return date1 >= date2;
    } else if (!workHours.touched) {
      return false;
    }
    return true;
  }

  private convertTimeToMinutes(value: any) {
    if (value.split(' ')[1] === 'min') {
      return parseInt(value.split(' ')[0]);
    } else if (value.split(' ')[1] === 'h') {
      if (value.split(' ').length > 2) {
        return (
          parseInt(value.split(' ')[0]) * 60 + parseInt(value.split(' ')[2])
        );
      }
      return parseInt(value.split(' ')[0]) * 60;
    }
    return 0;
  }

  base64toFile(base64String: string, fileName: string, mimeType: string): File {
    const base64Data = base64String.split(',')[1];
    const binaryString = atob(base64Data);
    const byteArray = new Uint8Array(binaryString.length);
    for (let i = 0; i < binaryString.length; i++) {
      byteArray[i] = binaryString.charCodeAt(i);
    }
    const blob = new Blob([byteArray], { type: mimeType });
    return new File([blob], fileName, { type: mimeType });
  }

  deleteField() {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: 'teren',
    });
    dialogRef.afterClosed().subscribe((reason: any) => {
      if (reason) {
        this.fieldService.deleteField(this.fieldId, reason).subscribe({
          next: () => {
            this.closeFormEvent.emit(this.addForm.controls.fieldName.value);
          },
        });
      }
    });
  }
}
