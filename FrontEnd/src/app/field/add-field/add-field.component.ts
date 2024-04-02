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

@Component({
  selector: 'app-add-field',
  templateUrl: './add-field.component.html',
  styleUrl: './add-field.component.css',
})
export class AddFieldComponent implements OnInit {
  private fieldService = inject(FieldService);
  private formBuilder = inject(FormBuilder);

  @Input() sportCenterId!: any;
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
    '1 h 15 min ',
    '1 h 30 min',
    '1 h 45 min',
    '2 h',
  ];
  timeSlots = [
    '5 min',
    '10 min',
    '15 min',
    '20 min ',
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
  }

  addField() {
    if (this.addForm.valid && this.fieldAvailability.includes(1)) {
      const formData = new FormData();
      formData.append('fieldName', this.addForm.controls.fieldName.value);
      formData.append('sportId', this.addForm.controls.sport.value);
      formData.append('minResTime', this.addForm.controls.minResTime.value);
      formData.append('timeSlot', this.addForm.controls.timeSlot.value);
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
      formData.append(
        'fieldAvailabilities',
        JSON.stringify(fieldAvailabilities),
      );
      formData.forEach((value, key) => {
        console.log(key + ':' + value);
      });
      // console.log(formData);
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
  }

  onDeleteImg(id: number) {
    this.url.splice(id, 1);
    const images = this.addForm.get('images') as FormArray;
    images.removeAt(id);
  }

  CloseForm() {
    this.closeFormEvent.emit();
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
    if (!workHours.invalid) {
      let date1 = new Date(`1970-01-01T${workHours.value.split('-')[0]}:00`);
      let date2 = new Date(`1970-01-01T${workHours.value.split('-')[1]}:00`);
      return date1 >= date2;
    }
    return true;
  }
}
