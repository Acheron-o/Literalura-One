#!/bin/bash

# BookVerse Test Execution Script
# This script runs all automated tests for the BookVerse application
# including unit tests, integration tests, and database tests

set -e  # Exit on any error

# Configuration
PROJECT_NAME="bookverse"
TEST_TIMEOUT=300  # 5 minutes
MAVEN_OPTS="-Dorg.slf4j.simpleLogger.defaultLogLevel=warn"

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
    echo -e "${color}🧪 $message${NC}"
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
    echo -e "${PURPLE}🧪 $1${NC}"
}

# Function to print section headers
print_section() {
    echo ""
    echo "════════════════════════════════════════════════════════════"
    print_header "$1"
    echo "════════════════════════════════════════════════════════════"
}

# Function to display test banner
display_test_banner() {
    echo ""
    echo "🧪 BOOKVERSE TEST SUITE"
    echo "════════════════════════════════════════════════════════════"
    echo "Running comprehensive test suite for BookVerse application..."
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

# Function to check if test environment is ready
check_test_environment() {
    print_section "Checking Test Environment"
    
    # Check if docker-compose configuration is valid
    if ! docker-compose config > /dev/null 2>&1; then
        print_error "Docker Compose configuration is invalid"
        exit 1
    fi
    
    # Check if PostgreSQL is running
    if ! docker-compose ps postgres | grep -q "Up"; then
        print_info "Starting PostgreSQL for tests..."
        docker-compose up -d postgres
        
        print_info "Waiting for PostgreSQL to be ready..."
        local count=0
        local max_attempts=30
        
        while [ $count -lt $max_attempts ]; do
            if docker-compose exec -T postgres pg_isready -U bookverse_user -d bookverse > /dev/null 2>&1; then
                print_success "PostgreSQL is ready for testing"
                break
            fi
            
            count=$((count + 1))
            echo -n "."
            sleep 2
        done
        
        if [ $count -eq $max_attempts ]; then
            print_error "PostgreSQL failed to start for testing"
            exit 1
        fi
    else
        print_success "PostgreSQL is already running"
    fi
}

# Function to run all tests
run_all_tests() {
    print_section "Running All Tests"
    
    print_info "Executing complete test suite with Maven..."
    
    if timeout $TEST_TIMEOUT docker-compose run --rm \
        --entrypoint "mvn clean test $MAVEN_OPTS" \
        test; then
        print_success "All tests completed successfully"
        return 0
    else
        local exit_code=$?
        print_error "Test suite failed with exit code: $exit_code"
        return $exit_code
    fi
}

# Function to display test results summary
display_test_summary() {
    local overall_status=$1
    
    print_section "Test Results Summary"
    
    echo ""
    if [ $overall_status -eq 0 ]; then
        echo "🎉 TEST SUITE STATUS: PASSED"
        echo "════════════════════════════════════════════════════════════"
        print_success "All tests completed successfully!"
        echo ""
        echo "📊 Test Categories Executed:"
        echo "   ✅ Unit Tests"
        echo "   ✅ Integration Tests"
        echo "   ✅ Repository Tests"
        echo "   ✅ Service Tests"
        echo ""
        echo "📈 Quality Metrics:"
        echo "   - Test Reports: Available in target/surefire-reports/"
        echo ""
    else
        echo "❌ TEST SUITE STATUS: FAILED"
        echo "════════════════════════════════════════════════════════════"
        print_error "Some tests failed. Please check the logs above."
        echo ""
        echo "🔍 Debugging Information:"
        echo "   - Check test logs: docker-compose logs test"
        echo "   - Run specific test: docker-compose run --rm test mvn test -Dtest=ClassName"
        echo "   - View detailed reports: target/surefire-reports/"
        echo ""
    fi
    
    echo "📚 Next Steps:"
    if [ $overall_status -eq 0 ]; then
        echo "   - Start application: ./scripts/start.sh"
    else
        echo "   - Fix failing tests"
        echo "   - Run tests again: ./scripts/test.sh"
        echo "   - Check test logs for details"
    fi
    echo ""
}

# Function to cleanup test environment
cleanup_test_environment() {
    print_info "Cleaning up test environment..."
    
    # Remove test container
    docker-compose rm -f test 2>/dev/null || true
    
    print_success "Test environment cleaned up"
}

# Function to handle script interruption
handle_interrupt() {
    echo ""
    print_warning "Test execution interrupted by user"
    cleanup_test_environment
    exit 1
}

# Set up signal handlers
trap handle_interrupt INT TERM

# Main execution
main() {
    local overall_status=0
    
    display_test_banner
    
    # Pre-test checks
    check_docker
    check_test_environment
    
    # Run tests
    run_all_tests || overall_status=$?
    
    # Cleanup and summary
    cleanup_test_environment
    display_test_summary $overall_status
    
    exit $overall_status
}

# Execute main function
main "$@"
