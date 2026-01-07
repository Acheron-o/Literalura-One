#!/bin/bash

# BookVerse Application Startup Script
# This script starts the BookVerse application with all necessary services
# including the database and provides an interactive console interface

set -e  # Exit on any error

# Configuration
PROJECT_NAME="bookverse"
POSTGRES_STARTUP_TIMEOUT=60
APPLICATION_STARTUP_TIMEOUT=30

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    local color="$1"
    local message="$2"
    echo -e "${color}🚀 $message${NC}"
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
    echo -e "${PURPLE}📚 $1${NC}"
}

# Function to print section headers
print_section() {
    echo ""
    echo "════════════════════════════════════════════════════════════"
    print_header "$1"
    echo "════════════════════════════════════════════════════════════"
}

# Function to display BookVerse banner
display_banner() {
    echo ""
    echo "███████╗███████╗████████╗██╗   ██╗██████╗ ███████╗"
    echo "██╔════╝██╔════╝╚══██╔══╝╚██╗ ██╔╝██╔══██╗██╔════╝"
    echo "███████╗█████╗     ██║    ╚████╔╝ ██████╔╝█████╗  "
    echo "╚════██║██╔══╝     ██║     ╚██╔╝  ██╔══██╗██╔══╝  "
    echo "███████║███████╗   ██║      ██║   ██████╔╝███████╗"
    echo "╚══════╝╚══════╝   ╚═╝      ╚═╝   ╚═════╝ ╚══════╝"
    echo ""
    echo "📖 Interactive Library Management System"
    echo "════════════════════════════════════════════════════════════"
    echo ""
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running or not accessible"
        print_info "Please start Docker and ensure you have the necessary permissions"
        exit 1
    fi
    print_success "Docker is running and accessible"
}

# Function to check if the environment is built
check_environment() {
    if ! docker-compose config > /dev/null 2>&1; then
        print_error "Docker Compose configuration is invalid"
        print_info "Please run './scripts/build.sh' first to build the environment"
        exit 1
    fi
    
    # Check if images exist
    if ! docker images | grep -q "bookverse-app"; then
        print_warning "BookVerse application image not found"
        print_info "Building the application image..."
        docker-compose build app
    fi
    
    print_success "Environment validation completed"
}

# Function to start PostgreSQL
start_postgresql() {
    print_section "Starting PostgreSQL Database"
    
    # Check if PostgreSQL is already running
    if docker-compose ps postgres | grep -q "Up"; then
        print_info "PostgreSQL is already running"
        return 0
    fi
    
    print_info "Starting PostgreSQL container..."
    docker-compose up -d postgres
    
    print_info "Waiting for PostgreSQL to become healthy..."
    local count=0
    local max_attempts=30
    
    while [ $count -lt $max_attempts ]; do
        if docker-compose exec -T postgres pg_isready -U bookverse_user -d bookverse > /dev/null 2>&1; then
            print_success "PostgreSQL is ready and healthy"
            return 0
        fi
        
        count=$((count + 1))
        echo -n "."
        sleep 2
    done
    
    print_error "PostgreSQL failed to start within the expected time"
    print_info "Check the logs with: docker-compose logs postgres"
    exit 1
}

# Function to start the application
start_application() {
    print_section "Starting BookVerse Application"
    
    print_info "Launching BookVerse interactive console..."
    echo ""
    echo "🎮 Starting interactive mode..."
    echo "   Use Ctrl+C to exit the application"
    echo "   The console will appear below:"
    echo ""
    
    # Start the application in interactive mode
    # Using docker-compose run ensures proper stdin/stdout handling
    if docker-compose run --rm --service-ports app; then
        print_success "BookVerse application exited normally"
    else
        local exit_code=$?
        if [ $exit_code -eq 130 ]; then  # Ctrl+C
            print_info "Application interrupted by user"
        else
            print_warning "Application exited with code: $exit_code"
        fi
    fi
}

# Function to display startup information
display_startup_info() {
    print_section "Application Information"
    
    echo ""
    echo "📊 Service Status:"
    echo "   - PostgreSQL: $(docker-compose ps postgres | grep -q 'Up' && echo 'Running' || echo 'Stopped')"
    echo "   - Application: Interactive mode"
    echo ""
    
    echo "🔗 Connection Information:"
    echo "   - Database Host: localhost:5432"
    echo "   - Database Name: bookverse"
    echo "   - Database User: bookverse_user"
    echo ""
    
    echo "📚 Available Commands:"
    echo "   - Search books online"
    echo "   - List registered books"
    echo "   - List registered authors"
    echo "   - Filter by language"
    echo "   - View statistics"
    echo ""
}

# Function to cleanup on exit
cleanup_on_exit() {
    echo ""
    print_info "Cleaning up application container..."
    docker-compose down --remove-orphans 2>/dev/null || true
    print_success "Cleanup completed"
}

# Function to handle script interruption
handle_interrupt() {
    echo ""
    print_warning "Application startup interrupted by user"
    cleanup_on_exit
    exit 1
}

# Set up signal handlers
trap handle_interrupt INT TERM
trap cleanup_on_exit EXIT

# Main execution
main() {
    display_banner
    
    print_info "Initializing BookVerse application startup..."
    
    # Pre-startup checks
    check_docker
    check_environment
    
    # Startup sequence
    start_postgresql
    display_startup_info
    
    # Start the application
    start_application
    
    print_success "BookVerse session completed! 👋"
}

# Execute main function
main "$@"
