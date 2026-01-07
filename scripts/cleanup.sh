#!/bin/bash

# BookVerse Environment Cleanup Script
# This script performs a complete cleanup of the Docker environment
# removing all containers, volumes, networks, and images related to the project

set -e  # Exit on any error

echo "🧹 Starting comprehensive BookVerse environment cleanup..."
echo "════════════════════════════════════════════════════════════"

# Function to print section headers
print_section() {
    echo ""
    echo "📋 $1"
    echo "─────────────────────────────────────────────────────────"
}

# Function to execute command with error handling
execute_command() {
    local description="$1"
    local command="$2"
    
    echo "⏳ $description..."
    if eval "$command" 2>/dev/null; then
        echo "✅ Successfully completed: $description"
    else
        echo "⚠️  Warning: $description (may not exist)"
    fi
}

print_section "Stopping and removing containers"

# Stop all running containers
execute_command "Stopping all BookVerse containers" "docker-compose down --remove-orphans --timeout 30"

# Remove any remaining containers
execute_command "Removing orphaned containers" "docker container prune -f --filter 'label=com.docker.compose.project=bookverse' 2>/dev/null || true"

print_section "Removing volumes"

# Remove PostgreSQL data volume
execute_command "Removing PostgreSQL data volume" "docker volume rm bookverse_postgres_data 2>/dev/null || true"

# Remove any other project volumes
execute_command "Removing unused volumes" "docker volume prune -f --filter 'label=com.docker.compose.project=bookverse' 2>/dev/null || true"

print_section "Removing networks"

# Remove custom networks
execute_command "Removing BookVerse networks" "docker network rm bookverse_bookverse-network 2>/dev/null || true"

# Remove unused networks
execute_command "Removing unused networks" "docker network prune -f 2>/dev/null || true"

print_section "Removing images"

# Remove application images
execute_command "Removing BookVerse application image" "docker rmi bookverse-app:latest 2>/dev/null || true"

# Remove dangling images
execute_command "Removing dangling images" "docker rmi \$(docker images -f 'dangling=true' -q) 2>/dev/null || true"

# Remove project-related images
execute_command "Removing project-related images" "docker image prune -f --filter 'label=com.docker.compose.project=bookverse' 2>/dev/null || true"

print_section "Final cleanup"

# Remove any remaining build cache
execute_command "Removing build cache" "docker builder prune -f 2>/dev/null || true"

# System-wide cleanup
execute_command "Performing system-wide cleanup" "docker system prune -a -f --volumes 2>/dev/null || true"

print_section "Cleanup summary"

echo ""
echo "🎉 BookVerse environment cleanup completed successfully!"
echo "════════════════════════════════════════════════════════════"
echo ""

# Display disk space recovery
echo "💾 Disk space recovery summary:"
docker system df

echo ""
echo "📊 Cleanup statistics:"
echo "   - Containers: Removed all BookVerse containers"
echo "   - Volumes: Removed all project volumes"
echo "   - Networks: Removed all custom networks"
echo "   - Images: Removed all project images"
echo "   - Cache: Cleared Docker build cache"

echo ""
echo "🚀 Your BookVerse environment is now completely clean!"
echo "   You can now run './scripts/build.sh' to rebuild everything from scratch."
echo ""
echo "════════════════════════════════════════════════════════════"
