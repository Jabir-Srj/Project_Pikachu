# Airline Customer Service System

## Project Overview

This is a comprehensive JavaFX-based desktop application for airline customer service that provides a complete solution for flight booking, customer management, support ticketing, and AI-powered customer assistance. The application implements a modern, user-friendly interface based on professional Figma designs and follows enterprise-level software architecture patterns.

## Demo Accounts
- admin / 123456
- customer / 123456

## Recent Updates (July 2025)

### ✅ Latest Enhancements
- **Status Validation**: Enforced strict status validation across all system entities
- **FAQ System**: Converted to modal alert window for better user experience
- **Customer Pages**: Added scrollable functionality with custom styling
- **Booking System**: Complete overhaul with admin/user management and booking creation
- **Refund Management**: Enhanced approval workflow with admin controls
- **AI Assistant**: Modern UI redesign with streaming responses and quick actions
- **Navigation**: Improved role-based navigation and modal interactions

### 🔧 Technical Improvements
- Strict status validation for bookings, flights, tickets, refunds, and payments
- Modal dialog implementation for FAQ system (no page navigation required)
- ScrollPane integration for responsive content handling
- Enhanced error handling and null checks across all controllers
- Improved code quality with consistent naming conventions
- Cleaned up unused files and optimized project structure

## Key Features

### User Management
- **Account Registration**: Secure user registration with role assignment
- **Authentication**: Secure login system with role-based access control
- **User Roles**: Customer, Admin, and Airline Management with different access levels
- **Customer Profile Management**: View and edit customer details

### Flight Management
- **Flight Search**: Advanced search with filters for destination, date, and preferences
- **Flight Information**: Detailed flight schedules, pricing, and availability
- **Flight Details**: Comprehensive flight information including routes, timing, and aircraft details
- **Real-time Updates**: Flight status tracking and notifications
- **Status Validation**: Enforced flight status consistency (SCHEDULED, BOARDING, DEPARTED, ARRIVED, DELAYED, CANCELLED, ON_TIME)

### Booking System
- **Flight Booking**: Complete booking process with passenger details
- **Booking Overview**: Comprehensive view of all customer bookings with admin/user functionality
- **Booking Details**: Detailed booking information with passenger details
- **Booking Management**: Modify, cancel, or view booking history
- **Status Validation**: Strict booking status enforcement (PENDING, CONFIRMED, CANCELLED)

### Payment Processing
- **Simplified Payment Flow**: Direct booking creation without separate payment confirmation
- **Booking Integration**: Payment processing handled at booking level
- **Multiple Payment Methods**: Support for various payment options (planned for future)
- **Secure Processing**: Payment information handled securely
- **Status Validation**: Payment status tracking (PENDING, COMPLETED, FAILED, REFUNDED)

### Support Ticketing System
- **Ticket Submission**: Easy-to-use support ticket creation
- **Ticket Overview**: Dashboard view of all support tickets
- **Ticket Status Tracking**: Real-time status updates for submitted tickets
- **Ticket Management**: View, reply, and manage customer support requests
- **Email Integration**: Automated ticket replies via email
- **Status Validation**: Ticket status enforcement (OPEN, IN_PROGRESS, ESCALATED, RESOLVED, REJECTED, CLOSED)

### AI-Powered Customer Support
- **Modern AI Chatbot**: Intelligent customer support using Langchain4j with streaming responses
- **Natural Language Processing**: Smart responses to customer queries
- **24/7 Availability**: Round-the-clock automated customer assistance
- **Quick Actions**: Pre-defined buttons for common queries
- **Connection Status**: Real-time connection monitoring

### Refund Management
- **Refund Requests**: Easy refund request submission
- **Enhanced Approval System**: Complete admin approval workflow with detailed review interface
- **Refund Tracking**: Status tracking for refund requests
- **Document Management**: Support for viewing refund-related documents
- **Admin Notes**: Detailed review comments and approval/rejection reasons
- **Status Validation**: Refund status enforcement (PENDING, APPROVED, REJECTED, PROCESSED)

### Admin Dashboard
- **Customer Management**: Comprehensive customer database management
- **Flight Administration**: Flight scheduling, pricing, and management  
- **Ticket Management**: Support ticket oversight and resolution
- **System Analytics**: Performance metrics and reporting
- **Refund Oversight**: Review and approve customer refund requests

### UI/UX Enhancements
- **Responsive Design**: Scrollable customer pages with custom styling
- **Modal Interactions**: FAQ system as modal dialogs for better UX
- **Modern Components**: Enhanced UI with gradients, shadows, and animations
- **Consistent Branding**: Pikachu Airlines yellow theme throughout application

## Technical Architecture

### Design Pattern
- **Model-View-Controller (MVC)**: Clean separation of concerns
- **Data Access Object (DAO)**: Abstracted data access layer
- **Service Layer**: Business logic encapsulation

### Technology Stack
- **Frontend**: JavaFX with FXML for modern UI design
- **Backend**: Java 23+ with service-oriented architecture
- **Data Storage**: JSON-based file system for data persistence
- **AI Integration**: Langchain4j for intelligent chatbot functionality
- **Build Tool**: Maven for dependency management and build automation

### UI/UX Design
- **Professional Design**: Based on modern Figma design specifications
- **Responsive Layout**: Adaptive interface for different screen sizes
- **Intuitive Navigation**: User-friendly interface with clear navigation patterns
- **Consistent Styling**: Uniform design language across all screens
- **Modern Components**: Enhanced UI with gradients, shadows, and animations

## Project Structure

```
Project_Pikachu/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── model/              # Data models and entities
│   │   │   │   ├── User.java       # User management models
│   │   │   │   ├── Flight.java     # Flight information models
│   │   │   │   ├── Booking.java    # Booking system models
│   │   │   │   ├── Ticket.java     # Support ticket models
│   │   │   │   └── ...             # Other domain models
│   │   │   ├── service/            # Business logic services
│   │   │   │   ├── UserService.java
│   │   │   │   ├── FlightService.java
│   │   │   │   ├── BookingService.java
│   │   │   │   └── TicketService.java
│   │   │   ├── controller/         # JavaFX controllers
│   │   │   │   ├── BookingOverviewController.java ✅ Enhanced
│   │   │   │   ├── RefundController.java ✅ Enhanced
│   │   │   │   ├── ModernChatbotController.java ✅ Modern AI Interface
│   │   │   │   ├── CustomerOverviewController.java ✅ Scrollable + FAQ Modal
│   │   │   │   └── ...             # Other controllers
│   │   │   ├── dao/               # Data access objects
│   │   │   │   ├── UserDAO.java
│   │   │   │   ├── FlightDAO.java
│   │   │   │   ├── BookingDAO.java
│   │   │   │   └── TicketDAO.java
│   │   │   ├── util/              # Utility classes
│   │   │   │   ├── DataManager.java
│   │   │   │   ├── NavigationManager.java ✅ Updated
│   │   │   │   └── FAQAlert.java ✅ New Modal FAQ System
│   │   │   └── AirlineApp.java    # Main application entry point
│   │   └── resources/
│   │       ├── fxml/              # FXML UI definitions
│   │       │   ├── BookingOverview.fxml ✅ Updated
│   │       │   ├── RefundApproval.fxml ✅ Working
│   │       │   ├── ModernAIChatbot.fxml ✅ Modern Interface
│   │       │   ├── CustomerOverview.fxml ✅ Scrollable
│   │       │   ├── CustomerProfile.fxml ✅ Scrollable  
│   │       │   └── ...             # Other FXML files
│   │       ├── css/               # Stylesheets
│   │       │   └── application.css ✅ Enhanced with FAQ & Scroll Styling
│   │       └── data/              # JSON data files
│   │           ├── users.json
│   │           ├── flights.json
│   │           ├── bookings.json
│   │           ├── tickets.json
│   │           ├── faqs.json
│   │           └── refunds.json
│   └── test/                      # Unit tests
├── assignmentGuide/               # Project documentation and designs
│   └── design/                    # Figma design files
├── target/                        # Compiled classes and resources
├── pom.xml                        # Maven configuration
├── FIXES_IMPLEMENTATION_SUMMARY.md ✅ Updated
└── README.md                      # Project documentation
```

## Setup and Installation

### Prerequisites
- **Java Development Kit (JDK)**: Version 23 or higher
- **JavaFX SDK**: Version 23 or higher (included with JDK)
- **Maven**: For dependency management
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd Project_Pikachu
   ```

2. **Install Dependencies**
   ```bash
   mvn clean install
   ```

3. **Run the Application**
   ```bash
   mvn javafx:run
   ```
   
   Or run the main class directly:
   ```bash
   java -cp target/classes AirlineApp
   ```

## Usage Instructions

### For Customers
1. **Login**: Use customer account credentials
2. **Search Flights**: Browse available flights with filters
3. **Book Flights**: Select flights and complete booking process
4. **Manage Bookings**: View, modify, or cancel existing bookings
5. **Support**: Submit tickets or use AI chatbot for assistance
6. **Refunds**: Request refunds for eligible bookings

### For Administrators
1. **Login**: Use admin account credentials
2. **Dashboard**: View system overview and statistics
3. **Customer Management**: Manage customer accounts and data
4. **Flight Management**: Add, modify, or remove flights
5. **Booking Oversight**: Monitor and manage all bookings
6. **Support Management**: Handle customer support tickets
7. **Refund Approval**: Review and process refund requests

## Status Validation System

The application enforces strict status validation across all entities to ensure data consistency:

### Booking Status
- **PENDING**: Booking created but not yet confirmed
- **CONFIRMED**: Booking confirmed and active
- **CANCELLED**: Booking cancelled by user or admin

### Flight Status
- **SCHEDULED**: Flight scheduled for future departure
- **BOARDING**: Passengers boarding the aircraft
- **DEPARTED**: Flight has departed from origin
- **ARRIVED**: Flight has arrived at destination
- **DELAYED**: Flight delayed from scheduled time
- **CANCELLED**: Flight cancelled
- **ON_TIME**: Flight operating on schedule

### Ticket Status
- **OPEN**: Support ticket created and awaiting response
- **IN_PROGRESS**: Ticket being worked on by support team
- **ESCALATED**: Ticket escalated to higher level support
- **RESOLVED**: Ticket resolved successfully
- **REJECTED**: Ticket rejected with reason
- **CLOSED**: Ticket closed after resolution

### Refund Status
- **PENDING**: Refund request submitted and under review
- **APPROVED**: Refund approved for processing
- **REJECTED**: Refund request rejected
- **PROCESSED**: Refund processed and completed

### Payment Status
- **PENDING**: Payment initiated but not completed
- **COMPLETED**: Payment successfully processed
- **FAILED**: Payment processing failed
- **REFUNDED**: Payment refunded to customer

## Troubleshooting

### Common Issues
1. **Application won't start**: Ensure Java 23+ and JavaFX are installed
2. **Login issues**: Verify demo account credentials are correct
3. **Data not loading**: Check JSON data files are present in resources/data/
4. **UI rendering issues**: Ensure CSS files are properly loaded

### Performance Tips
- Close unused application windows to free memory
- Restart application if experiencing slow performance
- Ensure adequate system resources for smooth operation

## Contributing

This project follows standard software development practices:
- Clean code principles
- Comprehensive error handling
- Proper documentation
- Consistent naming conventions
- Modular architecture design

## License

This project is developed for educational and demonstration purposes. All rights reserved.

## Contact

For questions or support regarding this airline management system, please refer to the project documentation or contact the development team. 
