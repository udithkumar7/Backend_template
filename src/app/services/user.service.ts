import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { environment } from '../../environments/environment';
import { tap } from 'rxjs/operators';

export interface UserProfile {
  id: number;
  username: string;
  email: string;
  roles: string[];
  accountNonLocked: boolean;
  accountExpiryDate: string;
  avatarLetter: string;
  enabled: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = environment.apiUrl;
  private currentUserSubject = new BehaviorSubject<UserProfile | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Try to load user data if we have a token
    if (localStorage.getItem('accessToken')) {
      this.loadUserProfile();
    }
  }

  loadUserProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.baseUrl}/users/profile`).pipe(
      tap(user => {
        this.currentUserSubject.next(user);
      })
    );
  }

  updateProfile(data: Partial<UserProfile>): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.baseUrl}/users/profile`, data).pipe(
      tap(user => {
        this.currentUserSubject.next(user);
      })
    );
  }

  changePassword(currentPassword: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/users/change-password`, {
      currentPassword,
      newPassword
    });
  }

  getCurrentUser(): UserProfile | null {
    return this.currentUserSubject.value;
  }

  clearUserData(): void {
    this.currentUserSubject.next(null);
  }
} 