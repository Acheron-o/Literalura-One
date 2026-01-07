#!/bin/bash

# BookVerse Idempotency Test Script
# This script tests the idempotency of the build process by running
# multiple rebuild cycles to ensure consistent results

set -e  # Exit on any error

# Configuration
PROJECT_NAME="bookverse"
TEST_CYCLES=3
CYCLE_DELAY=5

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

print_cycle() {
    echo -e "${CYAN}🔄 $1${NC}"
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
    echo "🧪 BOOKVERSE IDEMPOTENCY TEST"
    echo "════════════════════════════════════════════════════════════"
    echo "Testing build process idempotency with $TEST_CYCLES cycles..."
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

# Function to check if rebuild script exists
check_rebuild_script() {
    if [ ! -f "./scripts/rebuild.sh" ]; then
        print_error "Rebuild script not found at ./scripts/rebuild.sh"
        print_info "Please ensure the rebuild script is available"
        exit 1
    fi
    print_success "Rebuild script found"
}

# Function to run a single rebuild cycle
run_rebuild_cycle() {
    local cycle_num="$1"
    
    print_cycle "Starting Rebuild Cycle $cycle_num of $TEST_CYCLES"
    echo "─────────────────────────────────────────────────────────"
    
    local start_time=$(date +%s)
    
    # Run the rebuild script
    if ./scripts/rebuild.sh; then
        local end_time=$(date +%s)
        local duration=$((end_time - start_time))
        
        print_success "Cycle $cycle_num completed successfully in ${duration}s"
        
        # Record cycle result
        echo "$(date): Cycle $cycle_num: SUCCESS (${duration}s)" >> /tmp/bookverse_idempotency_test.log
        
        return 0
    else
        local end_time=$(date +%s)
        local duration=$((end_time - start_time))
        
        print_error "Cycle $cycle_num failed after ${duration}s"
        
        # Record cycle result
        echo "$(date): Cycle $cycle_num: FAILED (${duration}s)" >> /tmp/bookverse_idempotency_test.log
        
        return 1
    fi
}

# Function to display cycle progress
display_cycle_progress() {
    local current="$1"
    local total="$2"
    
    echo ""
    print_info "Progress: $current/$total cycles completed"
    
    # Calculate progress bar
    local progress=$((current * 100 / total))
    local filled=$((progress / 5))
    local empty=$((20 - filled))
    
    echo -n "["
    for ((i=0; i<filled; i++)); do echo -n "█"; done
    for ((i=0; i<empty; i++)); do echo -n "░"; done
    echo "] $progress%"
}

# Function to display test results
display_test_results() {
    local success_count="$1"
    local total_cycles="$2"
    
    print_section "Idempotency Test Results"
    
    echo ""
    if [ $success_count -eq $total_cycles ]; then
        echo "🎉 IDEMPOTENCY TEST: PASSED"
        echo "════════════════════════════════════════════════════════════"
        print_success "All $total_cycles rebuild cycles completed successfully!"
        echo ""
        echo "✅ Test Results:"
        echo "   - Successful cycles: $success_count/$total_cycles"
        echo "   - Success rate: 100%"
        echo "   - Build process is fully idempotent"
        echo ""
    else
        echo "❌ IDEMPOTENCY TEST: FAILED"
        echo "════════════════════════════════════════════════════════════"
        print_error "Only $success_count out of $total_cycles cycles completed successfully"
        echo ""
        echo "❌ Test Results:"
        echo "   - Successful cycles: $success_count/$total_cycles"
        echo "   - Success rate: $(( success_count * 100 / total_cycles ))%"
        echo "   - Build process is NOT idempotent"
        echo ""
    fi
    
    # Display detailed log if available
    if [ -f "/tmp/bookverse_idempotency_test.log" ]; then
        echo "📋 Detailed Log:"
        echo "─────────────────────────────────────────────────────────"
        cat /tmp/bookverse_idempotency_test.log
        echo ""
    fi
    
    echo "📊 Performance Analysis:"
    echo "─────────────────────────────────────────────────────────"
    if [ -f "/tmp/bookverse_idempotency_test.log" ]; then
        local avg_time=$(grep "SUCCESS" /tmp/bookverse_idempotency_test.log | \
            awk '{print $NF}' | sed 's/s//' | awk '{sum+=$1; count++} END {if(count>0) print int(sum/count)}')
        
        if [ -n "$avg_time" ]; then
            echo "   - Average rebuild time: ${avg_time}s"
            echo "   - Consistency: $([ $success_count -eq $total_cycles ] && echo "Perfect" || echo "Inconsistent")"
        fi
    fi
    echo ""
}

# Function to cleanup test artifacts
cleanup_test_artifacts() {
    print_info "Cleaning up test artifacts..."
    rm -f /tmp/bookverse_idempotency_test.log 2>/dev/null || true
    print_success "Test artifacts cleaned up"
}

# Function to handle script interruption
handle_interrupt() {
    echo ""
    print_warning "Idempotency test interrupted by user"
    cleanup_test_artifacts
    exit 1
}

# Function to prompt user confirmation
prompt_user_confirmation() {
    echo ""
    print_warning "This test will run $TEST_CYCLES complete rebuild cycles."
    print_info "Each cycle will clean, rebuild, and test the entire environment."
    print_info "This process may take several minutes to complete."
    echo ""
    
    read -p "Do you want to continue? (y/N): " -n 1 -r
    echo ""
    
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "Idempotency test cancelled by user"
        exit 0
    fi
}

# Set up signal handlers
trap handle_interrupt INT TERM

# Main execution
main() {
    local success_count=0
    
    display_test_banner
    
    # Pre-test checks
    check_docker
    check_rebuild_script
    
    # User confirmation
    prompt_user_confirmation
    
    # Initialize test log
    echo "BookVerse Idempotency Test Started: $(date)" > /tmp/bookverse_idempotency_test.log
    echo "Test Cycles: $TEST_CYCLES" >> /tmp/bookverse_idempotency_test.log
    echo "" >> /tmp/bookverse_idempotency_test.log
    
    # Run test cycles
    for ((i=1; i<=TEST_CYCLES; i++)); do
        echo ""
        print_section "Cycle $i of $TEST_CYCLES"
        
        if run_rebuild_cycle $i; then
            success_count=$((success_count + 1))
        fi
        
        display_cycle_progress $i $TEST_CYCLES
        
        # Wait between cycles (except after the last one)
        if [ $i -lt $TEST_CYCLES ]; then
            print_info "Waiting $CYCLE_DELAY seconds before next cycle..."
            sleep $CYCLE_DELAY
        fi
    done
    
    # Display results
    display_test_results $success_count $TEST_CYCLES
    
    # Cleanup
    cleanup_test_artifacts
    
    # Exit with appropriate code
    if [ $success_count -eq $TEST_CYCLES ]; then
        print_success "Idempotency test completed successfully! 🎉"
        exit 0
    else
        print_error "Idempotency test failed!"
        exit 1
    fi
}

# Function to display usage information
usage() {
    echo "Usage: $0 [options]"
    echo ""
    echo "Options:"
    echo "  -c, --cycles NUM    Number of test cycles (default: 3)"
    echo "  -d, --delay SEC    Delay between cycles in seconds (default: 5)"
    echo "  -h, --help         Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                  # Run 3 cycles with 5s delay"
    echo "  $0 -c 5 -d 10     # Run 5 cycles with 10s delay"
    echo ""
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -c|--cycles)
            TEST_CYCLES="$2"
            shift 2
            ;;
        -d|--delay)
            CYCLE_DELAY="$2"
            shift 2
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            usage
            exit 1
            ;;
    esac
done

# Validate arguments
if ! [[ "$TEST_CYCLES" =~ ^[0-9]+$ ]] || [ "$TEST_CYCLES" -lt 1 ]; then
    print_error "Number of cycles must be a positive integer"
    exit 1
fi

if ! [[ "$CYCLE_DELAY" =~ ^[0-9]+$ ]] || [ "$CYCLE_DELAY" -lt 0 ]; then
    print_error "Delay must be a non-negative integer"
    exit 1
fi

# Execute main function
main "$@"
