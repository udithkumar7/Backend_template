import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LayoutWrapperComponent } from '../../shared/layout/layout-wrapper.component';
import { CodeService, Code } from '../../services/code.service';
import { ToastService } from '../../services/toast.service';
import { ToastComponent } from '../../components/toast/toast.component';

@Component({
  selector: 'app-code-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, LayoutWrapperComponent, ToastComponent],
  template: `
    <app-layout-wrapper pageTitle="Code Management">
      <div class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 p-6">
        <!-- Header Section -->
        <div class="max-w-4xl mx-auto mb-8">
          <div class="text-center">
            <h1 class="text-4xl font-bold text-gray-900 mb-4">Location Management</h1>
            <p class="text-lg text-gray-600">Manage countries, states, and cities with dynamic cascading dropdowns</p>
          </div>
        </div>

        <!-- Main Content Card -->
        <div class="max-w-4xl mx-auto">
          <div class="bg-white rounded-2xl shadow-xl border border-gray-100 overflow-hidden">
            <!-- Card Header -->
            <div class="bg-gradient-to-r from-blue-600 to-purple-600 px-8 py-6">
              <div class="flex items-center justify-between">
                <div class="flex items-center space-x-4">
                  <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                    <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
                    </svg>
                  </div>
                  <div>
                    <h2 class="text-2xl font-bold text-white">Location Selection</h2>
                    <p class="text-blue-100">Choose your location from the dropdowns below</p>
                  </div>
                </div>
                <div class="text-right">
                  <div class="text-white/80 text-sm">Total Locations</div>
                  <div class="text-2xl font-bold text-white">{{ totalLocations }}</div>
                </div>
              </div>
            </div>

            <!-- Loading State -->
            <div *ngIf="loading" class="p-8">
              <div class="flex items-center justify-center py-12">
                <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                <span class="ml-4 text-gray-600 text-lg">Loading locations...</span>
              </div>
            </div>

            <!-- Error State -->
            <div *ngIf="error" class="p-8">
              <div class="bg-red-50 border border-red-200 rounded-xl p-6">
                <div class="flex items-center">
                  <svg class="w-6 h-6 text-red-400 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                  </svg>
                  <span class="text-red-800 text-lg">{{ error }}</span>
                </div>
              </div>
            </div>

            <!-- Main Content -->
            <div *ngIf="!loading && !error" class="p-8">
              <!-- Dynamic Dropdowns Section -->
              <div class="mb-8">
                <h3 class="text-xl font-semibold text-gray-900 mb-6">Location Selection</h3>
                
                <div class="space-y-6">
                  <!-- Country Dropdown - Always Visible -->
                  <div class="space-y-2">
                    <label class="block text-sm font-medium text-gray-700">Country</label>
                    <div class="relative">
                      <select 
                        [(ngModel)]="selectedCountry" 
                        (change)="onCountryChange()"
                        class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white shadow-sm">
                        <option value="">Select Country</option>
                        <option *ngFor="let country of countries" [value]="country.keycode">
                          {{ country.valuekey }}
                        </option>
                      </select>
                      <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                        <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                        </svg>
                      </div>
                    </div>
                  </div>

                  <!-- State Dropdown - Only visible when country is selected -->
                  <div *ngIf="selectedCountry" class="space-y-2 animate-fadeIn">
                    <label class="block text-sm font-medium text-gray-700">State/Province</label>
                    <div class="relative">
                      <select 
                        [(ngModel)]="selectedState" 
                        (change)="onStateChange()"
                        class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white shadow-sm">
                        <option value="">Select State</option>
                        <option *ngFor="let state of states" [value]="state.keycode">
                          {{ state.valuekey }}
                        </option>
                      </select>
                      <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                        <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                        </svg>
                      </div>
                    </div>
                  </div>

                  <!-- City Dropdown - Only visible when state is selected -->
                  <div *ngIf="selectedState" class="space-y-2 animate-fadeIn">
                    <label class="block text-sm font-medium text-gray-700">City</label>
                    <div class="relative">
                      <select 
                        [(ngModel)]="selectedCity" 
                        (change)="onCityChange()"
                        class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white shadow-sm">
                        <option value="">Select City</option>
                        <option *ngFor="let city of cities" [value]="city.keycode">
                          {{ city.valuekey }}
                        </option>
                      </select>
                      <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                        <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                        </svg>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Selected Location Display -->
                <div *ngIf="selectedLocation" class="mt-6 p-4 bg-gradient-to-r from-green-50 to-blue-50 rounded-xl border border-green-200 animate-fadeIn">
                  <div class="flex items-center space-x-3">
                    <div class="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                      <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                      </svg>
                    </div>
                    <div>
                      <h4 class="font-semibold text-gray-900">Selected Location</h4>
                      <p class="text-gray-600">{{ selectedLocation }}</p>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Action Buttons -->
              <div class="flex flex-col sm:flex-row gap-4 mb-8">
                <button 
                  (click)="showCreateForm = true" 
                  class="flex-1 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white px-6 py-3 rounded-xl font-semibold shadow-lg hover:shadow-xl transition-all duration-200 flex items-center justify-center space-x-2">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                  </svg>
                  <span>Add New Location</span>
                </button>
                <button 
                  (click)="resetSelection()" 
                  class="px-6 py-3 border border-gray-300 text-gray-700 rounded-xl font-semibold hover:bg-gray-50 transition-all duration-200">
                  Reset Selection
                </button>
              </div>

              <!-- Statistics Cards -->
              <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <div class="bg-gradient-to-r from-blue-500 to-blue-600 rounded-xl p-6 text-white">
                  <div class="flex items-center justify-between">
                    <div>
                      <p class="text-blue-100 text-sm">Countries</p>
                      <p class="text-2xl font-bold">{{ countries.length }}</p>
                    </div>
                    <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                      </svg>
                    </div>
                  </div>
                </div>

                <div class="bg-gradient-to-r from-green-500 to-green-600 rounded-xl p-6 text-white">
                  <div class="flex items-center justify-between">
                    <div>
                      <p class="text-green-100 text-sm">States</p>
                      <p class="text-2xl font-bold">{{ states.length }}</p>
                    </div>
                    <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-1.447-.894L15 4m0 13V4m0 0L9 7"></path>
                      </svg>
                    </div>
                  </div>
                </div>

                <div class="bg-gradient-to-r from-purple-500 to-purple-600 rounded-xl p-6 text-white">
                  <div class="flex items-center justify-between">
                    <div>
                      <p class="text-purple-100 text-sm">Cities</p>
                      <p class="text-2xl font-bold">{{ cities.length }}</p>
                    </div>
                    <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                      </svg>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Recent Locations Table -->
              <div class="bg-gray-50 rounded-xl p-6">
                <h3 class="text-xl font-semibold text-gray-900 mb-4">Recent Locations</h3>
                <div class="overflow-x-auto">
                  <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-white">
                      <tr>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Code</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                      </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                      <tr *ngFor="let code of recentCodes" class="hover:bg-gray-50">
                        <td class="px-6 py-4 whitespace-nowrap">
                          <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                                [class]="getCategoryBadgeClass(code.category)">
                            {{ code.category || 'N/A' }}
                          </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ code.valuekey }}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ code.keycode }}</td>
                        <td class="px-6 py-4 whitespace-nowrap">
                          <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                                [class]="code.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                            {{ code.isActive ? 'Active' : 'Inactive' }}
                          </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          <button 
                            (click)="editCode(code)"
                            class="text-blue-600 hover:text-blue-900 mr-3">
                            Edit
                          </button>
                          <button 
                            (click)="deleteCode(code.id!)"
                            class="text-red-600 hover:text-red-900">
                            Delete
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Create/Edit Code Modal -->
        <div *ngIf="showCreateForm || showEditForm" class="fixed inset-0 bg-black bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div class="relative top-20 mx-auto p-5 border w-full max-w-md shadow-lg rounded-2xl bg-white">
            <div class="mt-3">
              <div class="bg-gradient-to-r from-blue-600 to-purple-600 -m-5 mb-6 p-5 rounded-t-2xl">
                <h3 class="text-xl font-bold text-white">
                  {{ showEditForm ? 'Edit Location' : 'Add New Location' }}
                </h3>
                <p class="text-blue-100 text-sm mt-1">Configure location details</p>
              </div>
              
              <form [formGroup]="codeForm" (ngSubmit)="saveCode()" class="space-y-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Keycode</label>
                  <input 
                    type="text" 
                    formControlName="keycode"
                    [readonly]="showEditForm"
                    class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                  <div *ngIf="codeForm.get('keycode')?.invalid && codeForm.get('keycode')?.touched" class="text-red-500 text-sm mt-1">
                    Keycode is required and must be 2-10 characters
                  </div>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Name</label>
                  <input 
                    type="text" 
                    formControlName="valuekey"
                    class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                  <div *ngIf="codeForm.get('valuekey')?.invalid && codeForm.get('valuekey')?.touched" class="text-red-500 text-sm mt-1">
                    Name is required and must be 1-100 characters
                  </div>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Category</label>
                  <select 
                    formControlName="category"
                    class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                    <option value="">Select Category</option>
                    <option value="COUNTRY">Country</option>
                    <option value="STATE">State</option>
                    <option value="CITY">City</option>
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Parent Location</label>
                  <select 
                    formControlName="parentCode"
                    class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                    <option value="">No Parent (Root)</option>
                    <option *ngFor="let parent of parentCodes" [value]="parent.id">{{ parent.valuekey }} ({{ parent.keycode }})</option>
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Display Order</label>
                  <input 
                    type="number" 
                    formControlName="displayOrder"
                    min="0"
                    class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Description</label>
                  <textarea 
                    formControlName="description"
                    rows="3"
                    class="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500"></textarea>
                </div>
                <div class="flex items-center">
                  <input 
                    type="checkbox" 
                    formControlName="isActive"
                    class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
                  <label class="ml-2 text-sm text-gray-700">Active</label>
                </div>
                <div class="flex gap-3 pt-4">
                  <button 
                    type="submit" 
                    [disabled]="codeForm.invalid || saving"
                    class="flex-1 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 disabled:from-gray-400 disabled:to-gray-400 text-white px-6 py-3 rounded-xl font-semibold shadow-lg">
                    {{ saving ? 'Saving...' : (showEditForm ? 'Update Location' : 'Create Location') }}
                  </button>
                  <button 
                    type="button" 
                    (click)="closeForm()"
                    class="flex-1 px-6 py-3 border border-gray-300 text-gray-700 rounded-xl font-semibold hover:bg-gray-50">
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </app-layout-wrapper>
    
    <!-- Toast Notifications -->
    <app-toast></app-toast>
  `,
  styles: [`
    .animate-fadeIn {
      animation: fadeIn 0.3s ease-in-out;
    }
    
    @keyframes fadeIn {
      from {
        opacity: 0;
        transform: translateY(-10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
  `]
})
export class CodeManagementComponent implements OnInit {
  codes: Code[] = [];
  countries: Code[] = [];
  states: Code[] = [];
  cities: Code[] = [];
  parentCodes: Code[] = [];
  recentCodes: Code[] = [];
  
  selectedCountry: string = '';
  selectedState: string = '';
  selectedCity: string = '';
  selectedLocation: string = '';
  
  loading = false;
  saving = false;
  error = '';
  
  showCreateForm = false;
  showEditForm = false;
  editingCode: Code | null = null;

  codeForm: FormGroup;

  constructor(
    private codeService: CodeService,
    private toastService: ToastService,
    private fb: FormBuilder
  ) {
    this.codeForm = this.fb.group({
      keycode: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(10)]],
      valuekey: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(100)]],
      category: [''],
      parentCode: [''],
      displayOrder: [0, [Validators.min(0)]],
      description: [''],
      isActive: [true]
    });
  }

  ngOnInit(): void {
    this.loadData();
  }

  get totalLocations(): number {
    return this.codes.length;
  }

  loadData(): void {
    this.loading = true;
    this.error = '';

    this.codeService.getAll().subscribe({
      next: (codes) => {
        this.codes = codes;
        this.parentCodes = codes.filter(c => c.isActive);
        this.recentCodes = codes.slice(0, 10); // Show last 10 codes
        this.loadCountries();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load codes: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }

  loadCountries(): void {
    this.countries = this.codes.filter(c => c.category === 'COUNTRY' && c.isActive);
  }

  loadStates(): void {
    if (this.selectedCountry) {
      this.codeService.getByParentCode(this.selectedCountry).subscribe(states => {
        this.states = states.filter(s => s.category === 'STATE' && s.isActive);
        this.selectedState = '';
        this.selectedCity = '';
        this.cities = [];
        this.updateSelectedLocation();
      });
    } else {
      this.states = [];
      this.selectedState = '';
      this.selectedCity = '';
      this.cities = [];
      this.updateSelectedLocation();
    }
  }

  loadCities(): void {
    if (this.selectedState) {
      this.codeService.getByParentCode(this.selectedState).subscribe(cities => {
        this.cities = cities.filter(c => c.category === 'CITY' && c.isActive);
        this.selectedCity = '';
        this.updateSelectedLocation();
      });
    } else {
      this.cities = [];
      this.selectedCity = '';
      this.updateSelectedLocation();
    }
  }

  onCountryChange(): void {
    this.loadStates();
  }

  onStateChange(): void {
    this.loadCities();
  }

  onCityChange(): void {
    this.updateSelectedLocation();
  }

  updateSelectedLocation(): void {
    const country = this.countries.find(c => c.keycode === this.selectedCountry);
    const state = this.states.find(s => s.keycode === this.selectedState);
    const city = this.cities.find(c => c.keycode === this.selectedCity);

    if (country && state && city) {
      this.selectedLocation = `${city.valuekey}, ${state.valuekey}, ${country.valuekey}`;
    } else if (country && state) {
      this.selectedLocation = `${state.valuekey}, ${country.valuekey}`;
    } else if (country) {
      this.selectedLocation = country.valuekey;
    } else {
      this.selectedLocation = '';
    }
  }

  resetSelection(): void {
    this.selectedCountry = '';
    this.selectedState = '';
    this.selectedCity = '';
    this.selectedLocation = '';
    this.states = [];
    this.cities = [];
  }

  getCategoryBadgeClass(category: string | undefined): string {
    switch (category) {
      case 'COUNTRY':
        return 'bg-blue-100 text-blue-800';
      case 'STATE':
        return 'bg-green-100 text-green-800';
      case 'CITY':
        return 'bg-purple-100 text-purple-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  editCode(code: Code): void {
    this.editingCode = code;
    this.codeForm.patchValue({
      keycode: code.keycode,
      valuekey: code.valuekey,
      category: code.category || '',
      parentCode: code.parentCode?.id || '',
      displayOrder: code.displayOrder || 0,
      description: code.description || '',
      isActive: code.isActive
    });
    this.showEditForm = true;
  }

  saveCode(): void {
    if (this.codeForm.invalid) return;

    this.saving = true;
    const formData = this.codeForm.value;
    
    // Find parent code if selected
    if (formData.parentCode) {
      const parent = this.parentCodes.find(p => p.id === formData.parentCode);
      formData.parentCode = parent;
    } else {
      formData.parentCode = undefined;
    }

    if (this.showEditForm && this.editingCode) {
      // Update existing code
      this.codeService.updateCode(this.editingCode.id!, formData).subscribe({
        next: (updatedCode) => {
          const index = this.codes.findIndex(c => c.id === updatedCode.id);
          if (index > -1) {
            this.codes[index] = updatedCode;
          }
          this.closeForm();
          this.saving = false;
          this.toastService.showSuccess('Location updated successfully');
          this.loadData(); // Reload to refresh dropdowns
        },
        error: (err) => {
          this.saving = false;
          this.toastService.showError('Failed to update location: ' + (err.error?.message || err.message));
        }
      });
    } else {
      // Create new code
      this.codeService.createOrUpdate(formData).subscribe({
        next: (newCode) => {
          this.codes.push(newCode);
          this.closeForm();
          this.saving = false;
          this.toastService.showSuccess('Location created successfully');
          this.loadData(); // Reload to refresh dropdowns
        },
        error: (err) => {
          this.saving = false;
          this.toastService.showError('Failed to create location: ' + (err.error?.message || err.message));
        }
      });
    }
  }

  deleteCode(id: number): void {
    if (confirm('Are you sure you want to delete this location?')) {
      this.codeService.deleteById(id).subscribe({
        next: () => {
          this.codes = this.codes.filter(c => c.id !== id);
          this.recentCodes = this.codes.slice(0, 10);
          this.toastService.showSuccess('Location deleted successfully');
          this.loadData(); // Reload to refresh dropdowns
        },
        error: (err) => {
          this.toastService.showError('Failed to delete location: ' + (err.error?.message || err.message));
        }
      });
    }
  }

  closeForm(): void {
    this.showCreateForm = false;
    this.showEditForm = false;
    this.editingCode = null;
    this.codeForm.reset({ isActive: true, displayOrder: 0 });
  }
} 