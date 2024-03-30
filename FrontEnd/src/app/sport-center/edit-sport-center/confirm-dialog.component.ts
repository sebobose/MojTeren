import { Component, inject } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-dialog',
  template: `
    <h1 mat-dialog-title>Obriši sportski centar</h1>
    <div mat-dialog-content>
      Jeste li sigurni da želite obrisati ovaj sportski centar? Ova akcija je
      nepovratna.
    </div>
    <div mat-dialog-actions>
      <button mat-button (click)="onNoClick()">Ne</button>
      <button mat-button (click)="onYesClick()">Da</button>
    </div>
  `,
})
export class ConfirmDialogComponent {
  private dialogRef = inject(MatDialogRef<ConfirmDialogComponent>);

  onNoClick(): void {
    this.dialogRef.close(false);
  }

  onYesClick(): void {
    this.dialogRef.close(true);
  }
}
