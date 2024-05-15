import {
  ChangeDetectorRef,
  Component,
  inject,
  Input,
  OnDestroy,
  OnInit,
} from '@angular/core';

@Component({
  selector: 'app-carousel',
  templateUrl: './carousel.component.html',
  styleUrl: './carousel.component.css',
})
export class CarouselComponent implements OnInit, OnDestroy {
  @Input() slides: any = [];
  private changeDetectorRef = inject(ChangeDetectorRef);

  currentIndex: number = 0;
  timeoutId?: number;
  url: any;

  ngOnInit(): void {
    console.log(this.slides);
    this.url = 'data:image/png;base64, ' + this.slides[0];
    this.resetTimer();
  }

  ngOnDestroy() {
    window.clearTimeout(this.timeoutId);
  }

  resetTimer() {
    if (this.timeoutId) {
      window.clearTimeout(this.timeoutId);
    }
    this.timeoutId = window.setTimeout(() => this.goToNext(), 10000);
  }

  goToPrevious(): void {
    const isFirstSlide = this.currentIndex === 0;
    const newIndex = isFirstSlide
      ? this.slides.length - 1
      : this.currentIndex - 1;

    this.resetTimer();
    this.currentIndex = newIndex;
    this.url = 'data:image/png;base64, ' + this.slides[this.currentIndex];
    this.changeDetectorRef.detectChanges();
  }

  goToNext(): void {
    const isLastSlide = this.currentIndex === this.slides.length - 1;
    const newIndex = isLastSlide ? 0 : this.currentIndex + 1;

    this.resetTimer();
    this.currentIndex = newIndex;
    this.url = 'data:image/png;base64, ' + this.slides[this.currentIndex];
    this.changeDetectorRef.detectChanges();
  }

  goToSlide(slideIndex: number): void {
    this.resetTimer();
    this.url = 'data:image/png;base64, ' + this.slides[slideIndex];
    this.changeDetectorRef.detectChanges();
  }
}
