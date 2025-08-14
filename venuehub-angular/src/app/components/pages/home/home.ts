import { Component } from '@angular/core';
import { VenueList } from "../../list/venue-list/venue-list";
import { UserService } from '../../../services/user-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  imports: [VenueList, CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {

  isOwner = false;

  constructor(private userService: UserService){}

  ngOnInit(): void{
      this.userService.getCurrentUser().subscribe(user => {
        this.isOwner = user.role === 'OWNER';
      });
    }

}
