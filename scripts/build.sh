#!/bin/bash

# BookVerse Build Script
# This script builds the complete BookVerse environment from scratch
# including Docker images, containers, and database initialization

set -e  # Exit on any error

# Configuration
PROJECT_NAME="bookverse"
BUILD_TIMEOUT=300  # 5 minutes
POSTGRES_STARTUP_TIMEOUT=60  # 1 minute

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    local color="$1"
    local message="$2"
    echo -e "${color}🔧 $message${NC}"
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

# Function to print section headers
print_section() {
    echo ""
    echo "════════════════════════════════════════════════════════════"
    print_status "$BLUE" "$1"
    echo "════════════════════════════════════════════════════════════"
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

# Function to check if docker-compose is available
check_docker_compose() {
    if ! command -v docker-compose > /dev/null 2>&1; then
        print_error "docker-compose is not installed or not in PATH"
        print_info "Please install docker-compose to continue"
        exit 1
    fi
    print_success "docker-compose is available"
}

# Function to clean previous build artifacts
clean_previous_build() {
    print_section "Cleaning previous build artifacts"
    
    if [ -f "./scripts/cleanup.sh" ]; then
        print_info "Running cleanup script..."
        ./scripts/cleanup.sh
    else
        print_warning "Cleanup script not found, performing basic cleanup..."
        docker-compose down --remove-orphans 2>/dev/null || true
    fi
}

# Function to build Docker images
build_images() {
    print_section "Building Docker images"
    
    print_info "Building BookVerse application image..."
    if timeout $BUILD_TIMEOUT docker-compose build --no-cache --parallel; then
        print_success "Docker images built successfully"
    else
        print_error "Docker image build failed or timed out"
        exit 1
    fi
}

# Function to start PostgreSQL
start_postgresql() {
    print_section "Starting PostgreSQL database"
    
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

# Function to initialize database schema
initialize_database() {
    print_section "Initializing database schema"
    
    print_info "Running database migrations..."
    if docker-compose run --rm app --spring.profiles.active=migration; then
        print_success "Database schema initialized successfully"
    else
        print_warning "Database migration may have failed, but continuing..."
    fi
}

# Function to run health checks
run_health_checks() {
    print_section "Running health checks"
    
    # Check PostgreSQL
    if docker-compose exec -T postgres pg_isready -U bookverse_user -d bookverse > /dev/null 2>&1; then
        print_success "PostgreSQL health check passed"
    else
        print_error "PostgreSQL health check failed"
        return 1
    fi
    
    # Check application (if it's running)
    if docker-compose ps app | grep -q "Up"; then
        print_success "Application container is running"
    else
        print_warning "Application container is not running (this is normal for build-only mode)"
    fi
}

# Function to display build summary
display_summary() {
    print_section "Build Summary"
    
    echo ""
    print_info "Build completed successfully!"
    echo ""
    echo "📊 Build Statistics:"
    echo "   - Project: $PROJECT_NAME"
    echo "   - Build Time: $(date)"
    echo "   - Docker Images: $(docker images | grep bookverse | wc -l)"
    echo "   - Running Containers: $(docker-compose ps | grep 'Up' | wc -l)"
    echo ""
    
    echo "🚀 Next Steps:"
    echo "   1. Start the application: ./scripts/start.sh"
    echo "   2. Run tests: ./scripts/test.sh"
    echo "   3. View logs: ./scripts/logs.sh"
    echo "   4. Clean up: ./scripts/cleanup.sh"
    echo ""
    
    echo "📚 Useful Commands:"
    echo "   - View application logs: docker-compose logs -f app"
    echo "   - View database logs: docker-compose logs -f postgres"
    echo "   - Access database: docker-compose exec postgres psql -U bookverse_user -d bookverse"
    echo "   - Stop all services: docker-compose down"
    echo ""
}

# Function to handle script interruption
handle_interrupt() {
    print_error "Build process interrupted by user"
    print_info "Cleaning up partial build..."
    docker-compose down --remove-orphans 2>/dev/null || true
    exit 1
}

# Set up signal handlers
trap handle_interrupt INT TERM

# Main execution
main() {
    echo ""
    echo "🏗️  BookVerse Build Process"
    echo "════════════════════════════════════════════════════════════"
    echo "Building BookVerse environment from scratch..."
    echo ""
    
    # Pre-build checks
    check_docker
    check_docker_compose
    
    # Build process
    clean_previous_build
    build_images
    start_postgresql
    initialize_database
    run_health_checks
    
    # Display results
    display_summary
    
    print_success "BookVerse build process completed successfully! 🎉"
}

# Execute main function
main "$@"
