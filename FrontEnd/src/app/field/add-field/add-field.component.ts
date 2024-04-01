import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-add-field',
  templateUrl: './add-field.component.html',
  styleUrl: './add-field.component.css',
})
export class AddFieldComponent {
  @Input() sportCenterId!: any;
}
