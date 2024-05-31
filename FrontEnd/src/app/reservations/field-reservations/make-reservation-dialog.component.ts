import { Component, inject, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-make-reservation-dialog',
  template: `
    <div class="main-dialog">
      <div class="padding">
        <h1>Potvrda rezervacije</h1>
        <p>
          Sportski centar: {{ data.sportCenterName }} <br />
          Teren: {{ data.fieldName }} <br />
          Datum: {{ data.date }} <br />
          Početak: {{ data.startTime }} <br />
          Kraj: {{ data.endTime }} <br />
        </p>
        <textarea
          #reservationMessage
          class="reservation-message-input"
          placeholder="Ostavite poruku uz rezervaciju"
        ></textarea>
        <div class="buttons">
          <button class="button" (click)="onNoClick()">Odustani</button>
          <button class="button" (click)="onYesClick()">Potvrdi</button>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .main-dialog {
        background-color: #323437;
        height: 100%;
        width: 100%;
      }
      .padding {
        padding: 20px;
      }
      .reservation-message-input {
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
      button {
        margin: 10px;
      }
      .buttons {
        display: flex;
        justify-content: center;
      }
      p {
        font-size: 16px;
      }
      h1 {
        text-align: center;
      }
    `,
  ],
})
export class MakeReservationDialogComponent {
  private dialogRef = inject(MatDialogRef<MakeReservationDialogComponent>);
  public data = inject(MAT_DIALOG_DATA);
  @ViewChild('reservationMessage') reservationMessage!: any;

  onNoClick(): void {
    this.dialogRef.close();
  }

  onYesClick(): void {
    const reservationMessage = this.reservationMessage.nativeElement.value;
    this.dialogRef.close(['yes', reservationMessage]);
  }
}
