import { Component, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { Menu } from './components/pages/menu/menu';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Menu, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('venuehub-angular');

  
  constructor(private router: Router) {}

  get showNavbar(): boolean {
    const hiddenRoutes = ['/', '/register']; 
    return !hiddenRoutes.includes(this.router.url);
  }
}
