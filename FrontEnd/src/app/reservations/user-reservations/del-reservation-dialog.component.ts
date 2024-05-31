import { Component, inject, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-del-reservation-dialog',
  template: `
    <div class="delete-dialog">
      <h1>Otkaži rezervaciju</h1>
      <p class="yellow">
        Jeste li sigurni da želite otkazati ovu rezervaciju? Ova akcija je
        nepovratna.
      </p>
      <p>
        Sportski centar: {{ data.sportCenterName }} <br />
        Teren: {{ data.fieldName }} <br />
        Datum i vrijeme: {{ data.time }} <br />
        Sport: {{ data.sportName }}
      </p>
      <textarea
        #deleteReason
        class="delete-reason-input"
        placeholder="Unesite razlog otkazivanja..."
      ></textarea>
      <div class="buttons">
        <button class="button" (click)="onNoClick()">Odustani</button>
        <button class="button delete-button" (click)="onYesClick()">
          Potvrdi
        </button>
      </div>
      @if (showError) {
        <span class="form-error">Morate unijeti razlog otkazivanja!</span>
      }
    </div>
  `,
  styles: [
    `
      .delete-dialog {
        padding: 20px;
        background-color: #323437;
        height: 100%;
      }
      .delete-reason-input {
        font-size: 20px;
        border-radius: 10px;
        border: 1px solid #927979;
        background: transparent;
        margin-bottom: 20px;
        resize: none;
        height: 100px;
        width: 100%;
        color: #d1d0c5;

        &:focus {
          outline: none;
        }
      }
      .yellow {
        color: #e2b714;
      }
      button {
        margin: 10px;
      }
      .delete-button {
        background-color: #ff3f3f;
        color: #d1d0c5;
      }

      .delete-button:hover {
        background-color: #b8bec5;
        color: #646669;
      }
      .buttons {
        display: flex;
        justify-content: center;
      }
      .form-error {
        display: flex;
        justify-content: center;
      }
    `,
  ],
})
export class DelReservationDialogComponent {
  private dialogRef = inject(MatDialogRef<DelReservationDialogComponent>);
  public data = inject(MAT_DIALOG_DATA);
  @ViewChild('deleteReason') deleteReason!: any;
  showError = false;

  onNoClick(): void {
    this.dialogRef.close();
  }

  onYesClick(): void {
    const searchValue = this.deleteReason.nativeElement.value;
    if (searchValue === '') {
      this.showError = true;
      return;
    }
    this.dialogRef.close(searchValue);
  }
}
