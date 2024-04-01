import { Component, inject, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-dialog',
  template: `
    <div class="delete-dialog">
      <h1>Obriši sportski centar</h1>
      <p>
        Jeste li sigurni da želite obrisati ovaj sportski centar? Ova akcija je
        nepovratna.
      </p>
      <textarea
        #deleteReason
        class="delete-reason-input"
        placeholder="Unesite razlog brisanja..."
        (keydown.enter)="deleteSC(); $event.preventDefault()"
      ></textarea>
      <div>
        <button class="button" (click)="onNoClick()">Odustani</button>
        <button class="button delete-button" (click)="onYesClick()">
          Potvrdi
        </button>
        @if (showError) {
          <span class="form-error">Morate unijeti razlog brisanja!</span>
        }
      </div>
    </div>
  `,
  styles: [
    `
      .delete-dialog {
        padding: 20px;
        background-color: #323437;
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
      p {
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
    `,
  ],
})
export class ConfirmDialogComponent {
  private dialogRef = inject(MatDialogRef<ConfirmDialogComponent>);

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

  deleteSC() {}
}
