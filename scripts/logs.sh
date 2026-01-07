#!/bin/bash

# BookVerse Log Viewer Script
# This script provides an interactive interface to view logs from different services
# including application logs, database logs, and system logs

set -e  # Exit on any error

# Configuration
PROJECT_NAME="bookverse"
LOG_COLORS=true
FOLLOW_MODE=true

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    local color="$1"
    local message="$2"
    echo -e "${color}📋 $message${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_header() {
    echo -e "${PURPLE}📋 $1${NC}"
}

# Function to print section headers
print_section() {
    echo ""
    echo "════════════════════════════════════════════════════════════"
    print_header "$1"
    echo "════════════════════════════════════════════════════════════"
}

# Function to display log viewer banner
display_log_banner() {
    echo ""
    echo "📋 BOOKVERSE LOG VIEWER"
    echo "════════════════════════════════════════════════════════════"
    echo "Interactive log viewing for BookVerse services..."
    echo ""
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running or not accessible"
        print_info "Please start Docker and ensure you have necessary permissions"
        exit 1
    fi
    print_success "Docker is running and accessible"
}

# Function to check service status
check_service_status() {
    local service="$1"
    
    if docker-compose ps "$service" | grep -q "Up"; then
        echo -e "${GREEN}●${NC} $service: Running"
    elif docker-compose ps "$service" | grep -q "Exit"; then
        echo -e "${RED}●${NC} $service: Stopped (exited)"
    else
        echo -e "${YELLOW}●${NC} $service: Not found"
    fi
}

# Function to display service status overview
display_service_status() {
    print_section "Service Status Overview"
    
    echo ""
    echo "Current Service Status:"
    echo "─────────────────────────────────────────────────────────"
    check_service_status "app"
    check_service_status "postgres"
    check_service_status "test"
    echo ""
}

# Function to show application logs
show_application_logs() {
    local follow="$1"
    
    print_section "Application Logs"
    
    if ! docker-compose ps app | grep -q "Up"; then
        print_warning "Application container is not running"
        print_info "Start the application with: ./scripts/start.sh"
        return 1
    fi
    
    print_info "Displaying application logs..."
    if [ "$follow" = "true" ]; then
        print_info "Following logs (Press Ctrl+C to stop)..."
        docker-compose logs -f --tail=100 app
    else
        print_info "Showing recent logs (last 100 lines)..."
        docker-compose logs --tail=100 app
    fi
}

# Function to show database logs
show_database_logs() {
    local follow="$1"
    
    print_section "Database Logs"
    
    if ! docker-compose ps postgres | grep -q "Up"; then
        print_warning "PostgreSQL container is not running"
        print_info "Start the database with: docker-compose up -d postgres"
        return 1
    fi
    
    print_info "Displaying PostgreSQL logs..."
    if [ "$follow" = "true" ]; then
        print_info "Following logs (Press Ctrl+C to stop)..."
        docker-compose logs -f --tail=100 postgres
    else
        print_info "Showing recent logs (last 100 lines)..."
        docker-compose logs --tail=100 postgres
    fi
}

# Function to show all service logs
show_all_logs() {
    local follow="$1"
    
    print_section "All Service Logs"
    
    print_info "Displaying logs from all services..."
    if [ "$follow" = "true" ]; then
        print_info "Following logs (Press Ctrl+C to stop)..."
        docker-compose logs -f --tail=50
    else
        print_info "Showing recent logs (last 50 lines)..."
        docker-compose logs --tail=50
    fi
}

# Function to display interactive menu
show_interactive_menu() {
    while true; do
        clear
        display_log_banner
        display_service_status
        
        echo ""
        echo "📋 Log Viewing Options:"
        echo "─────────────────────────────────────────────────────────"
        echo "1) Application logs (follow)"
        echo "2) Application logs (static)"
        echo "3) Database logs (follow)"
        echo "4) Database logs (static)"
        echo "5) All service logs (follow)"
        echo "6) All service logs (static)"
        echo "0) Exit"
        echo ""
        
        read -p "Select an option [0-6]: " choice
        
        case $choice in
            1)
                show_application_logs "true"
                ;;
            2)
                show_application_logs "false"
                read -p "Press Enter to continue..."
                ;;
            3)
                show_database_logs "true"
                ;;
            4)
                show_database_logs "false"
                read -p "Press Enter to continue..."
                ;;
            5)
                show_all_logs "true"
                ;;
            6)
                show_all_logs "false"
                read -p "Press Enter to continue..."
                ;;
            0)
                print_success "Exiting log viewer..."
                exit 0
                ;;
            *)
                print_error "Invalid option. Please try again."
                sleep 2
                ;;
        esac
    done
}

# Function to display usage information
usage() {
    echo "Usage: $0 [option]"
    echo ""
    echo "Options:"
    echo "  app         Show application logs (follow mode)"
    echo "  app-static  Show application logs (static mode)"
    echo "  db          Show database logs (follow mode)"
    echo "  db-static   Show database logs (static mode)"
    echo "  all         Show all service logs (follow mode)"
    echo "  all-static  Show all service logs (static mode)"
    echo "  interactive Show interactive menu (default)"
    echo ""
    echo "Examples:"
    echo "  $0              # Show interactive menu"
    echo "  $0 app          # Follow application logs"
    echo "  $0 db-static    # Show static database logs"
    echo ""
}

# Function to handle script interruption
handle_interrupt() {
    echo ""
    print_info "Log viewing interrupted by user"
    exit 0
}

# Set up signal handlers
trap handle_interrupt INT TERM

# Main execution
main() {
    local option="${1:-interactive}"
    
    # Pre-checks
    check_docker
    
    case $option in
        "app")
            show_application_logs "true"
            ;;
        "app-static")
            show_application_logs "false"
            ;;
        "db")
            show_database_logs "true"
            ;;
        "db-static")
            show_database_logs "false"
            ;;
        "all")
            show_all_logs "true"
            ;;
        "all-static")
            show_all_logs "false"
            ;;
        "interactive"|*)
            show_interactive_menu
            ;;
    esac
}

# Check for help flag
if [[ "$1" == "-h" || "$1" == "--help" ]]; then
    usage
    exit 0
fi

# Execute main function
main "$@"
