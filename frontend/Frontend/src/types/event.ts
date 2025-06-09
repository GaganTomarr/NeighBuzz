// src/types/event.ts
export interface Organizer {
    name: string;
    email: string;
    // You can add other details like phone, website if needed
  }
  
  export interface Event {
    id: string;
    title: string;
    dateTime: string; // Consider using Date object and formatting for display
    description: string; // This will be the short summary for the card
    location: string;
    category?: string;
    imageUrl?: string;
    detailedDescription?: string; // For the expanded view
    capacity?: number; // For the expanded view
    organizerDetails?: Organizer; // For the expanded view
    registrationLink?: string; // For the "Register for Event" button
    // Add any other fields present in your detailed design
  }
  