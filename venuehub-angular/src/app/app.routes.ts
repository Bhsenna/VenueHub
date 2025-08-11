import { Routes } from '@angular/router';
import { Login } from './components/forms/login/login';
import { Register } from './components/forms/register/register';
import { Home } from './components/pages/home/home';
import { VenueDetails } from './components/venue-details/venue-details';

export const routes: Routes = [
    { path: '', component: Login },
    { path: 'register', component: Register },
    { path: 'home', component: Home },
    { path: 'venues/:id', component: VenueDetails }
];
