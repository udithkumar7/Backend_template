import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LayoutWrapperComponent } from '../../shared/layout/layout-wrapper.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, RouterModule, LayoutWrapperComponent],
  template: `
    <app-layout-wrapper pageTitle="User Management">
      <div class="p-6">
        <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
          <h1 class="text-2xl font-bold text-gray-900">User Management</h1>
          <p class="mt-1 text-gray-600">Manage system users and their permissions</p>
        </div>
        
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="text-center py-12">
            <div class="inline-block p-4 rounded-full bg-blue-100 mb-4">
              <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z"></path>
              </svg>
            </div>
            <h3 class="text-lg font-medium text-gray-900">User Management</h3>
            <p class="mt-2 text-sm text-gray-500">This feature is under development</p>
          </div>
        </div>
      </div>
    </app-layout-wrapper>
  `,
  styles: []
})
export class UsersComponent {
  constructor() {}
} 