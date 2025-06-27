import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { UserService, UserProfile } from '../../services/user.service';
import { LayoutWrapperComponent } from '../../shared/layout/layout-wrapper.component';
import { Subscription } from 'rxjs';

interface StatCard {
  title: string;
  value: string;
  change: string;
  changeType: 'increase' | 'decrease';
  icon: string;
  color: string;
}

interface UserData {
  name: string;
  email: string;
  role: string;
  avatar: string;
  avatarLetter: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, LayoutWrapperComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy {
  isLoading = true;
  isLoggingOut = false;
  private userSubscription?: Subscription;
  
  currentUser: UserData = {
    name: '',
    email: '',
    role: '',
    avatar: '',
    avatarLetter: ''
  };

  statCards: StatCard[] = [
    {
      title: 'Total Users',
      value: '1,234',
      change: '+8%',
      changeType: 'increase',
      icon: 'group',
      color: 'blue'
    },
    {
      title: 'Total Products',
      value: '456',
      change: '+12%',
      changeType: 'increase',
      icon: 'inventory',
      color: 'green'
    },
    {
      title: 'Orders Today',
      value: '89',
      change: '-3%',
      changeType: 'decrease',
      icon: 'shopping_cart',
      color: 'yellow'
    },
    {
      title: 'Revenue',
      value: '$12,345',
      change: '+15%',
      changeType: 'increase',
      icon: 'attach_money',
      color: 'purple'
    }
  ];

  ngOnInit() {
    this.userSubscription = this.userService.currentUser$.subscribe(user => {
      if (user) {
        this.currentUser = {
          name: user.username,
          email: user.email,
          role: user.roles[0] || 'User',
          avatar: '',
          avatarLetter: user.avatarLetter
        };
      }
      this.isLoading = false;
    });
  }

  ngOnDestroy() {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastService: ToastService,
    private userService: UserService
  ) {}

  logout() {
    if (this.isLoggingOut) return;
    
    this.isLoggingOut = true;
    this.authService.logout().subscribe({
      next: () => {
        this.toastService.showSuccess('Logged out successfully');
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Logout error:', error);
        this.toastService.showError('Failed to logout. Please try again.');
        this.isLoggingOut = false;
      }
    });
  }

  getStatColorClass(color: string): string {
    const colorMap: { [key: string]: string } = {
      blue: 'text-blue-600',
      green: 'text-green-600',
      yellow: 'text-yellow-600',
      purple: 'text-purple-600'
    };
    return colorMap[color] || 'text-gray-600';
  }

  getStatBgClass(color: string): string {
    const colorMap: { [key: string]: string } = {
      blue: 'bg-blue-100',
      green: 'bg-green-100',
      yellow: 'bg-yellow-100',
      purple: 'bg-purple-100'
    };
    return colorMap[color] || 'bg-gray-100';
  }
} 