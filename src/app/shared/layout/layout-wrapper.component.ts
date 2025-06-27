import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutComponent } from './layout.component';

@Component({
  selector: 'app-layout-wrapper',
  standalone: true,
  imports: [CommonModule, LayoutComponent],
  template: `
    <app-layout [pageTitle]="pageTitle">
      <ng-content></ng-content>
    </app-layout>
  `,
  styles: []
})
export class LayoutWrapperComponent {
  @Input() pageTitle: string = 'Dashboard';
} 