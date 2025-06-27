import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { MenuService, SidebarItem } from '../../services/menu.service';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { Subscription } from 'rxjs';

interface UserData {
  name: string;
  email: string;
  role: string;
  avatarLetter: string;
}

interface NotificationItem {
  id: string;
  message: string;
  type: 'success' | 'error' | 'info';
  timestamp: Date;
}

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarComponent],
  template: `
    <!-- Main Layout Container -->
    <div class="min-h-screen bg-gray-50">
      <!-- Dynamic Sidebar -->
      <app-sidebar 
        [isSidebarOpen]="isSidebarOpen"
        [currentUser]="currentUser"
        (sidebarToggle)="toggleSidebar()">
      </app-sidebar>

      <!-- Main Content Area -->
      <div [class]="'transition-all duration-300 ease-in-out ' + (isSidebarOpen ? 'ml-64' : 'ml-16')">
        <!-- Top Navigation Bar -->
        <nav class="bg-white shadow-sm h-16 flex items-center justify-between px-6 sticky top-0 z-30">
          <!-- Left side -->
          <div class="flex items-center space-x-4">
            <button (click)="toggleSidebar()" 
                    class="p-2 text-gray-500 hover:text-gray-700 rounded-lg hover:bg-gray-100">
              <span class="material-icons">menu</span>
            </button>
            <h1 class="text-xl font-semibold text-gray-900">{{ pageTitle }}</h1>
          </div>

          <!-- Right side -->
          <div class="flex items-center space-x-4">
            <!-- Notifications -->
            <div class="relative">
              <button class="p-2 text-gray-500 hover:text-gray-700 rounded-full hover:bg-gray-100 relative">
                <span class="material-icons">notifications</span>
                <span *ngIf="notifications.length > 0" 
                      class="absolute -top-1 -right-1 h-5 w-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                  {{ notifications.length }}
                </span>
              </button>
            </div>

            <!-- User Menu -->
            <div class="relative flex items-center space-x-3">
              <div class="h-8 w-8 rounded-full bg-blue-600 flex items-center justify-center text-white font-semibold text-sm">
                {{ currentUser.avatarLetter }}
              </div>
              <div class="hidden md:block">
                <p class="text-sm font-medium text-gray-900">{{ currentUser.name }}</p>
                <p class="text-xs text-gray-500">{{ currentUser.email }}</p>
              </div>
              <button (click)="onLogout()" 
                      class="p-2 text-gray-500 hover:text-gray-700 rounded-lg hover:bg-gray-100">
                <span class="material-icons">logout</span>
              </button>
            </div>
          </div>
        </nav>

        <!-- Page Content Slot -->
        <main class="min-h-screen">
          <ng-content></ng-content>
        </main>

        <!-- Footer -->
        <footer class="bg-white border-t border-gray-200 mt-auto p-4 text-center text-sm text-gray-600">
          <p>&copy; 2024 Your Company Name. All rights reserved.</p>
        </footer>
      </div>
    </div>

    <!-- Toast Notifications -->
    <div class="fixed top-4 right-4 z-50 space-y-4">
      <div *ngFor="let notification of notifications" 
           [class]="'p-4 rounded-lg shadow-lg flex items-start max-w-sm transition-all duration-300 ' +
                    (notification.type === 'success' ? 'bg-green-50 border-l-4 border-green-500' :
                     notification.type === 'error' ? 'bg-red-50 border-l-4 border-red-500' :
                     'bg-blue-50 border-l-4 border-blue-500')">
        <div class="flex-1">
          <p [class]="'text-sm font-medium ' +
                      (notification.type === 'success' ? 'text-green-800' :
                       notification.type === 'error' ? 'text-red-800' :
                       'text-blue-800')">
            {{ notification.message }}
          </p>
          <p class="text-xs text-gray-500 mt-1">{{ formatTimeAgo(notification.timestamp) }}</p>
        </div>
        <button (click)="dismissNotification(notification.id)" 
                class="ml-3 p-1 hover:bg-white hover:bg-opacity-50 rounded">
          <span class="material-icons text-sm text-gray-400">close</span>
        </button>
      </div>
    </div>
  `,
  styles: []
})
export class LayoutComponent implements OnInit, OnDestroy {
  @Input() pageTitle: string = 'Dashboard';
  @Input() isSidebarOpen: boolean = true;
  @Input() notifications: NotificationItem[] = [];

  @Output() sidebarToggle = new EventEmitter<void>();
  @Output() logout = new EventEmitter<void>();
  @Output() notificationDismiss = new EventEmitter<string>();

  currentUser: UserData = {
    name: 'User',
    email: 'user@example.com',
    role: 'User',
    avatarLetter: 'U'
  };

  private userSubscription?: Subscription;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    // Subscribe to user updates
    this.userSubscription = this.userService.currentUser$.subscribe(user => {
      if (user) {
        this.currentUser = {
          name: user.username,
          email: user.email,
          role: user.roles?.[0] || 'User',
          avatarLetter: user.avatarLetter || user.username.charAt(0).toUpperCase()
        };
      }
    });

    // Load initial user data
    this.userService.loadUserProfile().subscribe();
  }

  ngOnDestroy() {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
    this.sidebarToggle.emit();
  }

  onLogout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/auth/login']);
      },
      error: (error) => {
        console.error('Logout error:', error);
      }
    });
  }

  dismissNotification(id: string) {
    this.notificationDismiss.emit(id);
  }

  formatTimeAgo(date: Date): string {
    const now = new Date();
    const diffInMinutes = Math.floor((now.getTime() - date.getTime()) / (1000 * 60));
    
    if (diffInMinutes < 60) {
      return `${diffInMinutes} minutes ago`;
    } else {
      const diffInHours = Math.floor(diffInMinutes / 60);
      return `${diffInHours} hour${diffInHours > 1 ? 's' : ''} ago`;
    }
  }
} 