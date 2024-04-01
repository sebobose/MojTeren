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
  days = [
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
  fieldAvailability = new Array(7).fill(0);

  addForm = this.formBuilder.nonNullable.group(
    {
      fieldName: ['', [Validators.required]],
      sport: ['', [Validators.required]],
      minResTime: ['', [Validators.required]],
      timeSlot: ['', [Validators.required]],
      description: ['', [Validators.required]],
      Monday: [''],
      Tuesday: [''],
      Wednesday: [''],
      Thursday: [''],
      Friday: [''],
      Saturday: [''],
      Sunday: [''],
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
    // if (this.addForm.valid) {
    //   const formData = new FormData();
    //   formData.append('fieldName', this.addForm.controls.fieldName.value);
    //   formData.append('sport', this.addForm.value.sport);
    //   formData.append('minResTime', this.addForm.value.minResTime);
    //   formData.append('timeSlot', this.addForm.value.timeSlot);
    //   formData.append('description', this.addForm.value.description);
    //   for (let i of this.addForm.value.fieldAvailability) {
    //     formData.append('fieldAvailabilities', i);
    //   }
    //   for (let i of this.addForm.value.images) {
    //     formData.append('images', i);
    //   }
    //   this.fieldService.addField(formData, this.sportCenterId).subscribe({
    //     next: (data) => {
    //       this.closeFormEvent.emit();
    //     },
    //     error: (error) => {
    //       console.error('Error:', error);
    //     },
    //   });
    // }
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
    console.log(index);
    this.fieldAvailability[index] = this.fieldAvailability[index] === 0 ? 1 : 0;
  }
}
