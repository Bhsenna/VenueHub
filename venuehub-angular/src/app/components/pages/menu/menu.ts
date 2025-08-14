import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd, RouterModule } from '@angular/router';
import { UserService } from '../../../services/user-service';


@Component({
  selector: 'app-menu',
  imports: [RouterModule, CommonModule],
  templateUrl: './menu.html',
  styleUrl: './menu.css'
})
export class Menu implements OnInit {

  loggedIn = false;
  isOwner = false;

  constructor(private router: Router, private userService: UserService) {
    this.loggedIn = !!localStorage.getItem('token');

    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.loggedIn = !!localStorage.getItem('token');
      }
    });
  }

  ngOnInit(): void {
    if (this.loggedIn) {
      this.userService.getCurrentUser().subscribe(user => {
        this.isOwner = user.role === 'OWNER';
      });
    }
  }

  navigateToHome() {
    this.router.navigate(['/home']);
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/']);
  }

}
