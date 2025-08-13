import { Component } from '@angular/core';
import { VenueList } from "../../list/venue-list/venue-list";
import { ChatbotComponent } from '../../chatbot-component/chatbot-component';

@Component({
  selector: 'app-home',
  imports: [VenueList, ChatbotComponent],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {

}
