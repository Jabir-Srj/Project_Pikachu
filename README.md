# Airline Customer Service System

## Project Overview

This is a comprehensive JavaFX-based desktop application for airline customer service that provides a complete solution for flight booking, customer management, support ticketing, and AI-powered customer assistance. The application implements a modern, user-friendly interface based on professional Figma designs and follows enterprise-level software architecture patterns.

## Demo Accounts
- admin / 123456
- customer / 123456

## Recent Updates (July 2025)

### ✅ Major Fixes Implemented
- **Booking System**: Complete overhaul with admin/user management and booking creation
- **Refund Management**: Enhanced approval workflow with admin controls
- **AI Assistant**: Modern UI redesign with streaming responses and quick actions
- **Payment System**: Simplified flow to avoid FXML parsing issues
- **Navigation**: Improved role-based navigation and back button functionality

### 🔧 Technical Improvements
- Fixed FXML parsing errors and missing expression issues
- Enhanced error handling and null checks across all controllers
- Improved code quality with consistent naming conventions
- Added comprehensive integration testing

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

### Booking System
- **Flight Booking**: Complete booking process with passenger details
- **Booking Overview**: Comprehensive view of all customer bookings with admin/user functionality
- **Booking Details**: Detailed booking information with passenger details
- **Booking Management**: Modify, cancel, or view booking history

### Payment Processing
- **Simplified Payment Flow**: Direct booking creation without separate payment confirmation
- **Booking Integration**: Payment processing handled at booking level
- **Multiple Payment Methods**: Support for various payment options (planned for future)
- **Secure Processing**: Payment information handled securely

### Support Ticketing System
- **Ticket Submission**: Easy-to-use support ticket creation
- **Ticket Overview**: Dashboard view of all support tickets
- **Ticket Status Tracking**: Real-time status updates for submitted tickets
- **Ticket Management**: View, reply, and manage customer support requests
- **Email Integration**: Automated ticket replies via email

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

### Admin Dashboard
- **Customer Management**: Comprehensive customer database management
- **Flight Administration**: Flight scheduling, pricing, and management
- **Ticket Management**: Support ticket oversight and resolution
- **System Analytics**: Performance metrics and reporting
- **FAQ Management**: Maintain and update frequently asked questions

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
│   │   │   │   ├── ModernChatbotController.java ✅ New
│   │   │   │   └── ...             # Other controllers
│   │   │   ├── dao/               # Data access objects
│   │   │   │   ├── UserDAO.java
│   │   │   │   ├── FlightDAO.java
│   │   │   │   ├── BookingDAO.java
│   │   │   │   └── TicketDAO.java
│   │   │   ├── util/              # Utility classes
│   │   │   │   ├── DataManager.java
│   │   │   │   └── NavigationManager.java ✅ Updated
│   │   │   └── AirlineApp.java    # Main application entry point
│   │   └── resources/
│   │       ├── fxml/              # FXML UI definitions
│   │       │   ├── BookingOverview.fxml ✅ Updated
│   │       │   ├── RefundApproval.fxml ✅ Working
│   │       │   ├── ModernAIChatbot.fxml ✅ New
│   │       │   └── ...             # Other FXML files
│   │       ├── css/               # Stylesheets
│   │       │   └── application.css ✅ Enhanced
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

### Getting Started
1. **Launch the Application**: Run `AirlineApp.java`
2. **Login or Register**: Use the authentication screens to access the system
3. **Choose Your Role**: Select Customer, Admin, or Airline Management based on your access level

### For Customers
- **Search Flights**: Use the flight search functionality to find available flights
- **Book Flights**: Complete the booking process with passenger details (simplified payment flow)
- **Manage Bookings**: View, modify, or cancel existing bookings
- **Get Support**: Use the modern AI chatbot or submit support tickets for assistance
- **Request Refunds**: Submit refund requests for eligible bookings

### For Admins
- **Dashboard Access**: Use the admin dashboard for system overview
- **Manage Customers**: View and manage customer accounts
- **Handle Tickets**: Review and respond to customer support tickets
- **Approve Refunds**: Process refund requests with detailed review interface
- **System Management**: Maintain FAQs and system settings

### For Airline Management
- **Flight Management**: Add, update, and manage flight schedules
- **Booking Oversight**: Monitor and manage all booking activities
- **Customer Analytics**: View customer data and booking patterns
- **System Reports**: Generate reports and analytics

## Configuration

### Data Files
The application uses JSON files for data persistence located in `src/main/resources/data/`:
- `users.json`: User accounts and profiles
- `flights.json`: Flight schedules and information
- `bookings.json`: Booking records and details
- `tickets.json`: Support tickets and responses
- `faqs.json`: Frequently asked questions
- `refunds.json`: Refund requests and status

### AI Chatbot Configuration
The AI chatbot uses Langchain4j for intelligent responses. Configuration includes:
- FAQ integration for common queries
- Natural language processing for user interactions
- Automated response generation based on context
- Streaming responses for real-time interaction

## Recent Fixes and Improvements

### ✅ Completed Fixes
1. **Booking System**: Complete overhaul with admin/user management
2. **Refund Management**: Enhanced approval workflow with admin controls
3. **AI Assistant**: Modern UI redesign with streaming responses
4. **Payment System**: Simplified to avoid FXML parsing issues
5. **Navigation**: Improved role-based navigation

### 🔧 Technical Improvements
- Fixed FXML parsing errors and missing expression issues
- Enhanced error handling and null checks
- Improved code quality and consistency
- Added comprehensive integration testing

## Team Members
- **Ishaq Arham Mujthaba** (Group Leader) - 0378327
- **Jabir Iliyas Suraj-Deen** - 0377741
- **Isabel Sonja Tanudjaja** - 0373125
- **Hashir Ahmed** - 0375609
- **Jin Jianuo** - 0376898
- **Feliz Nicole Perez Rigor** - 0361983

## License
This project is developed as part of an academic assignment for ITS66704 course.

## 🤝 Contributing
This is an academic project. For any questions or suggestions, please contact the team members listed above.

---

**Note**: This application is designed for educational purposes and demonstrates modern software engineering practices in Java application development with JavaFX and AI integration. The recent updates have significantly improved the stability and functionality of the system. 
