import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-menu',
  imports: [RouterModule, CommonModule],
  templateUrl: './menu.html',
  styleUrl: './menu.css'
})
export class Menu {

  loggedIn = false;

  constructor(private router: Router) {
    this.loggedIn = !!localStorage.getItem('token');

    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.loggedIn = !!localStorage.getItem('token');
      }
    });
  }

  navigateToHome() {
    this.router.navigate(['/home']);
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/']);
  }


}
