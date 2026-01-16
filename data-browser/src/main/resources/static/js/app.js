// API Base URL
const API_BASE = '/api';

// State
let currentRegion = null;
let currentData = [];

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    initTabs();
    loadClusterStatus();
    loadRegions();
    setupEventListeners();
    initCreateRegionModal();
    
    // Auto-refresh cluster status every 5 seconds
    setInterval(loadClusterStatus, 5000);
});

// Tab switching
function initTabs() {
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabContents = document.querySelectorAll('.tab-content');
    
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const tabName = button.dataset.tab;
            
            // Update buttons
            tabButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            
            // Update contents
            tabContents.forEach(content => content.classList.remove('active'));
            document.getElementById(`${tabName}Tab`).classList.add('active');
            
            // Load data if needed
            if (tabName === 'regions') {
                loadRegions();
            }
        });
    });
}

// Setup event listeners
function setupEventListeners() {
    document.getElementById('loadDataBtn').addEventListener('click', () => {
        const regionName = document.getElementById('regionSelect').value;
        if (regionName) {
            loadRegionData(regionName);
        }
    });
    
    document.getElementById('refreshBtn').addEventListener('click', () => {
        loadRegions();
        if (currentRegion) {
            loadRegionData(currentRegion);
        }
    });
}

// Initialize create region modal
function initCreateRegionModal() {
    const modal = document.getElementById('createRegionModal');
    const createBtn = document.getElementById('createRegionBtn');
    const cancelBtn = document.getElementById('cancelCreateBtn');
    const closeBtn = document.querySelector('.close');
    const form = document.getElementById('createRegionForm');
    const regionTypeSelect = document.getElementById('regionType');
    const regionTypeDesc = document.getElementById('regionTypeDesc');
    
    createBtn.addEventListener('click', () => {
        modal.style.display = 'block';
    });
    
    closeBtn.addEventListener('click', () => {
        modal.style.display = 'none';
    });
    
    cancelBtn.addEventListener('click', () => {
        modal.style.display = 'none';
        form.reset();
        regionTypeDesc.textContent = '';
    });
    
    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            modal.style.display = 'none';
            form.reset();
            regionTypeDesc.textContent = '';
        }
    });
    
    // Update description when region type changes
    regionTypeSelect.addEventListener('change', () => {
        const selectedOption = regionTypeSelect.options[regionTypeSelect.selectedIndex];
        const desc = selectedOption.getAttribute('data-desc');
        regionTypeDesc.textContent = desc || '';
    });
    
    // Show description for initial selection
    const selectedOption = regionTypeSelect.options[regionTypeSelect.selectedIndex];
    const desc = selectedOption.getAttribute('data-desc');
    regionTypeDesc.textContent = desc || '';
    
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        await createRegion();
    });
}

// Create region
async function createRegion() {
    const regionName = document.getElementById('regionName').value.trim();
    const regionType = document.getElementById('regionType').value;
    
    if (!regionName) {
        alert('Please enter a region name');
        return;
    }
    
    const modal = document.getElementById('createRegionModal');
    const form = document.getElementById('createRegionForm');
    
    try {
        const response = await fetch(`${API_BASE}/regions/${encodeURIComponent(regionName)}?type=${regionType}`, {
            method: 'POST'
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert(`Region "${regionName}" created successfully!`);
            modal.style.display = 'none';
            form.reset();
            regionTypeDesc.textContent = '';
            // Wait a bit for proxy to be created, then reload regions list
            setTimeout(() => {
                loadRegions();
            }, 800);
        } else {
            alert(`Failed to create region: ${data.error || 'Unknown error'}`);
        }
    } catch (error) {
        console.error('Failed to create region:', error);
        alert(`Failed to create region: ${error.message}`);
    }
}

// Load cluster status
async function loadClusterStatus() {
    try {
        const response = await fetch(`${API_BASE}/cluster/status`);
        const data = await response.json();
        
        updateClusterStatus(data);
        updateClusterInfo(data);
    } catch (error) {
        console.error('Failed to load cluster status:', error);
        updateClusterStatus({ status: 'error', error: error.message });
    }
}

// Update cluster status indicator
function updateClusterStatus(data) {
    const indicator = document.getElementById('statusIndicator');
    const statusText = document.getElementById('statusText');
    
    indicator.className = 'status-indicator';
    
    if (data.status === 'connected') {
        indicator.classList.add('connected');
        statusText.textContent = `Connected - ${data.memberCount || 0} member(s)`;
    } else if (data.status === 'disconnected') {
        indicator.classList.add('disconnected');
        statusText.textContent = 'Disconnected';
    } else {
        indicator.classList.add('disconnected');
        statusText.textContent = `Error: ${data.error || 'Unknown error'}`;
    }
}

// Update cluster information
function updateClusterInfo(data) {
    const container = document.getElementById('clusterInfo');
    
    if (data.status !== 'connected') {
        container.innerHTML = `
            <div class="error">
                <strong>Connection Error:</strong> ${data.error || 'Unable to connect to GemFire cluster'}
            </div>
        `;
        return;
    }
    
    const membersHtml = data.members && data.members.length > 0
        ? data.members.map(member => `
            <div class="member-card">
                <h3>${member.name || member.id}</h3>
                <div class="member-details">
                    <div><strong>ID:</strong> ${member.id}</div>
                    <div><strong>Host:</strong> ${member.host || 'N/A'}</div>
                    <div><strong>Groups:</strong> ${member.groups && member.groups.length > 0 ? member.groups.join(', ') : 'None'}</div>
                </div>
            </div>
        `).join('')
        : '<p>No cluster members found</p>';
    
    container.innerHTML = `
        <div class="info-grid">
            <div class="info-item">
                <label>Status</label>
                <value>${data.status}</value>
            </div>
            <div class="info-item">
                <label>Cache Name</label>
                <value>${data.cacheName || 'N/A'}</value>
            </div>
            <div class="info-item">
                <label>Member Count</label>
                <value>${data.memberCount || 0}</value>
            </div>
        </div>
        <div class="members-list">
            <h3 style="margin-bottom: 15px; color: #333;">Cluster Members</h3>
            ${membersHtml}
        </div>
    `;
}

// Load regions
async function loadRegions() {
    try {
        const response = await fetch(`${API_BASE}/regions/info`);
        const data = await response.json();
        
        updateRegionsList(data);
        updateRegionSelect(data);
    } catch (error) {
        console.error('Failed to load regions:', error);
        document.getElementById('regionsList').innerHTML = `
            <div class="error">
                <strong>Error:</strong> Failed to load Regions: ${error.message}
            </div>
        `;
    }
}

// Update regions list
function updateRegionsList(regions) {
    const container = document.getElementById('regionsList');
    
    if (!regions || regions.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <p>No regions found</p>
            </div>
        `;
        return;
    }
    
    const regionsHtml = regions.map(region => `
        <div class="region-card" onclick="selectRegion('${region.name}')">
            <h3>${region.name}</h3>
            <div class="region-size">${region.size || 0} entries</div>
            <div class="region-path">${region.fullPath || ''}</div>
        </div>
    `).join('');
    
    container.innerHTML = `<div class="regions-grid">${regionsHtml}</div>`;
}

// Update region select dropdown
function updateRegionSelect(regions) {
    const select = document.getElementById('regionSelect');
    const currentValue = select.value;
    
    // Clear existing options except the first one
    while (select.children.length > 1) {
        select.removeChild(select.lastChild);
    }
    
    // Add regions
    regions.forEach(region => {
        const option = document.createElement('option');
        option.value = region.name;
        option.textContent = `${region.name} (${region.size || 0} entries)`;
        select.appendChild(option);
    });
    
    // Restore selection if still valid
    if (currentValue && Array.from(select.options).some(opt => opt.value === currentValue)) {
        select.value = currentValue;
    }
}

// Select region from card click
function selectRegion(regionName) {
    document.getElementById('regionSelect').value = regionName;
    loadRegionData(regionName);
    // Switch to data tab
    document.querySelector('[data-tab="data"]').click();
}

// Load region data
async function loadRegionData(regionName, limit = 100, offset = 0) {
    currentRegion = regionName;
    const container = document.getElementById('dataContent');
    
    container.innerHTML = '<div class="loading">Loading...</div>';
    
    try {
        const response = await fetch(`${API_BASE}/data/${encodeURIComponent(regionName)}?limit=${limit}&offset=${offset}`);
        const data = await response.json();
        
        if (!data.success) {
            container.innerHTML = `
                <div class="error">
                    <strong>Error:</strong> ${data.error || 'Failed to load data'}
                </div>
            `;
            return;
        }
        
        currentData = data.entries || [];
        displayData(data);
        
    } catch (error) {
        console.error('Failed to load region data:', error);
        container.innerHTML = `
            <div class="error">
                <strong>Error:</strong> Failed to load data: ${error.message}
            </div>
        `;
    }
}

// Display data in table
function displayData(data) {
    const container = document.getElementById('dataContent');
    
    if (!data.entries || data.entries.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <p>No data in this region</p>
            </div>
        `;
        return;
    }
    
    const tableRows = data.entries.map(entry => {
        const valueStr = JSON.stringify(entry.value, null, 2);
        const valueDisplay = typeof entry.value === 'string' 
            ? entry.value 
            : valueStr.length > 100 
                ? valueStr.substring(0, 100) + '...' 
                : valueStr;
        
        return `
            <tr>
                <td class="key-cell">${escapeHtml(String(entry.key))}</td>
                <td class="value-cell" title="${escapeHtml(valueStr)}">${escapeHtml(valueDisplay)}</td>
                <td class="value-type">${entry.valueType || 'N/A'}</td>
            </tr>
        `;
    }).join('');
    
    container.innerHTML = `
        <div style="margin-bottom: 15px;">
            <strong>Total Records:</strong> ${data.totalSize || 0} | 
            <strong>Displayed:</strong> ${data.returnedCount || 0}
        </div>
        <table class="data-table">
            <thead>
                <tr>
                    <th>Key</th>
                    <th>Value</th>
                    <th>Type</th>
                </tr>
            </thead>
            <tbody>
                ${tableRows}
            </tbody>
        </table>
    `;
}

// Utility: Escape HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
