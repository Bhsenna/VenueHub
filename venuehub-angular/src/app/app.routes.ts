import { Routes } from '@angular/router';
import { Login } from './components/forms/login/login';
import { Register } from './components/forms/register/register';
import { Home } from './components/pages/home/home';
import { EventList } from './components/list/event-list/event-list';
import { VenueDetails } from './components/pages/venue-details/venue-details';
import { EventDetails } from './components/pages/event-details/event-details';
import { ChatbotComponent } from './components/chatbot-component/chatbot-component';

export const routes: Routes = [
    { path: '', component: Login },
    { path: 'register', component: Register },
    { path: 'home', component: Home },
    { path: 'venues/:id', component: VenueDetails },
    { path: 'events', component: EventList},
    { path: 'events/:id', component: EventDetails },
    { path: 'chatbot', component: ChatbotComponent}

];
