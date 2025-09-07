// Main JavaScript file for J2J Transform Studio

// Global variables
let inputEditor, specEditor, outputEditor;
let autoTransformEnabled = false;
let lastValidInput = null;
let lastValidSpec = null;

// Initialize the application when the DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeEditors();
    initializeEventListeners();
    initializeDropdown();
    loadExamples();
});

// Initialize CodeMirror editors
function initializeEditors() {
    // Input Editor
    inputEditor = CodeMirror.fromTextArea(document.getElementById('inputEditor'), {
        mode: 'application/json',
        theme: 'material-darker',
        lineNumbers: true,
        autoCloseBrackets: true,
        matchBrackets: true,
        foldGutter: true,
        gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter'],
        extraKeys: {
            'Ctrl-Space': 'autocomplete',
            'Tab': 'indentMore',
            'Shift-Tab': 'indentLess'
        }
    });

    // Specification Editor
    specEditor = CodeMirror.fromTextArea(document.getElementById('specEditor'), {
        mode: 'application/json',
        theme: 'material-darker',
        lineNumbers: true,
        autoCloseBrackets: true,
        matchBrackets: true,
        foldGutter: true,
        gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter'],
        extraKeys: {
            'Ctrl-Space': 'autocomplete',
            'Tab': 'indentMore',
            'Shift-Tab': 'indentLess'
        }
    });

    // Output Editor
    outputEditor = CodeMirror.fromTextArea(document.getElementById('outputEditor'), {
        mode: 'application/json',
        theme: 'material-darker',
        lineNumbers: true,
        readOnly: true,
        foldGutter: true,
        gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter']
    });

    // Add change listeners for auto-transform
    inputEditor.on('change', handleEditorChange);
    specEditor.on('change', handleEditorChange);
}

// Initialize event listeners
function initializeEventListeners() {
    // Transform button
    document.getElementById('transformBtn').addEventListener('click', performTransform);
    
    // Auto-transform toggle
    document.getElementById('autoTransform').addEventListener('change', function(e) {
        autoTransformEnabled = e.target.checked;
        if (autoTransformEnabled && lastValidInput && lastValidSpec) {
            performTransform();
        }
    });
    
    // Format buttons
    document.getElementById('formatInputBtn').addEventListener('click', formatInput);
    document.getElementById('formatSpecBtn').addEventListener('click', formatSpec);
    
    // Clear buttons
    document.getElementById('clearInputBtn').addEventListener('click', clearInput);
    document.getElementById('clearSpecBtn').addEventListener('click', clearSpec);
    
    // Copy output button
    document.getElementById('copyOutputBtn').addEventListener('click', copyOutput);
    
    // Download button
    document.getElementById('downloadBtn').addEventListener('click', downloadOutput);
    
    // Toggle view button
    document.getElementById('toggleViewBtn').addEventListener('click', toggleView);
    
    // Quick start dropdown
    document.getElementById('quickStartBtn').addEventListener('click', toggleDropdown);
    
    // Close dropdown when clicking outside
    document.addEventListener('click', function(e) {
        const dropdown = document.getElementById('quickStartDropdown');
        const button = document.getElementById('quickStartBtn');
        if (!dropdown.contains(e.target) && e.target !== button) {
            dropdown.classList.remove('show');
        }
    });
    
    // Tools and settings modals
    document.getElementById('toolsBtn').addEventListener('click', showToolsModal);
    document.getElementById('settingsBtn').addEventListener('click', showSettingsModal);
    document.getElementById('closeToolsModal').addEventListener('click', closeToolsModal);
    document.getElementById('closeSettingsModal').addEventListener('click', closeSettingsModal);
    
    // Close modals when clicking outside
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                modal.classList.remove('show');
            }
        });
    });
}

// Initialize the quick start dropdown
function initializeDropdown() {
    const dropdownItems = document.querySelectorAll('.example-item');
    dropdownItems.forEach(item => {
        item.addEventListener('click', function() {
            const operation = this.getAttribute('data-operation');
            const example = this.getAttribute('data-example');
            loadExample(operation, example);
            document.getElementById('quickStartDropdown').classList.remove('show');
        });
    });
}

// Toggle the quick start dropdown
function toggleDropdown() {
    document.getElementById('quickStartDropdown').classList.toggle('show');
}

// Load example based on operation
function loadExample(operation, example) {
    // Update the current operation display
    const dropdownItems = document.querySelectorAll('.example-item');
    let exampleName = 'Chain - Multi-step';
    
    // Find the clicked item to get its name
    dropdownItems.forEach(item => {
        if (item.getAttribute('data-example') === example) {
            exampleName = item.querySelector('.example-header span').textContent;
        }
    });
    
    document.getElementById('currentOperation').textContent = exampleName;
    document.getElementById('operationType').value = operation;
    
    // Load example data based on operation and specific example
    switch(example) {
        case 'simple-shift':
            loadSimpleShiftExample();
            break;
        case 'attribute-reference':
            loadAttributeReferenceExample();
            break;
        case 'default-values':
            loadDefaultValuesExample();
            break;
        case 'identity':
            loadIdentityExample();
            break;
        case 'array-handling':
            loadArrayHandlingExample();
            break;
        case 'array-filtering':
            loadArrayFilteringExample();
            break;
        case 'nested-arrays':
            loadNestedArraysExample();
            break;
        case 'wildcards':
            loadWildcardsExample();
            break;
        case 'complex-nested':
            loadComplexNestedExample();
            break;
        case 'multi-operation':
            loadMultiOperationExample();
            break;
        case 'boolean-values':
            loadBooleanValuesExample();
            break;
        case 'null-values':
            loadNullValuesExample();
            break;
        case 'number-values':
            loadNumberValuesExample();
            break;
        default:
            // Load chain example as default
            loadChainExample();
    }
    
    // Perform transform if auto-transform is enabled
    if (autoTransformEnabled) {
        performTransform();
    }
}

// Get display name for operation
function getOperationDisplayName(operation) {
    const names = {
        'chain': 'Chain - Multi-step'
    };
    return names[operation] || 'Chain - Multi-step';
}

// Handle editor changes for auto-transform
function handleEditorChange() {
    if (autoTransformEnabled) {
        // Debounce the transform to avoid too many requests
        clearTimeout(window.transformTimeout);
        window.transformTimeout = setTimeout(performTransform, 500);
    }
    
    // Validate input and spec
    validateInput();
    validateSpec();
}

// Validate input JSON
function validateInput() {
    const input = inputEditor.getValue();
    if (!input.trim()) {
        updateValidationStatus('inputValidation', 'Ready', 'valid');
        return;
    }
    
    // Send validation request to backend
    fetch('/api/transform/validate/json', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: input
    })
    .then(response => response.json())
    .then(result => {
        if (result.valid) {
            updateValidationStatus('inputValidation', 'Valid', 'valid');
            lastValidInput = input;
        } else {
            updateValidationStatus('inputValidation', result.message, 'error');
            lastValidInput = null;
        }
        updateValidationMessage('inputValidationMessage', result);
    })
    .catch(error => {
        updateValidationStatus('inputValidation', 'Validation Error', 'error');
        updateValidationMessage('inputValidationMessage', {
            valid: false,
            message: 'Error validating JSON',
            details: error.message
        });
        lastValidInput = null;
    });
}

// Validate specification
function validateSpec() {
    const spec = specEditor.getValue();
    
    if (!spec.trim()) {
        updateValidationStatus('specValidation', 'Required', 'warning');
        return;
    }
    
    // Prepare validation request - only support chain operations
    const requestData = {
        operation: 'chain',
        chainSpec: spec
    };
    
    // Send validation request to backend
    fetch('/api/transform/validate/spec', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
    .then(response => response.json())
    .then(result => {
        if (result.valid) {
            updateValidationStatus('specValidation', 'Valid', 'valid');
            lastValidSpec = spec;
        } else {
            updateValidationStatus('specValidation', result.message, 'error');
            lastValidSpec = null;
        }
        updateValidationMessage('specValidationMessage', result);
    })
    .catch(error => {
        updateValidationStatus('specValidation', 'Validation Error', 'error');
        updateValidationMessage('specValidationMessage', {
            valid: false,
            message: 'Error validating specification',
            details: error.message
        });
        lastValidSpec = null;
    });
}

// Update validation status display
function updateValidationStatus(elementId, text, status) {
    const element = document.getElementById(elementId);
    const icon = element.querySelector('.validation-icon');
    const textElement = element.querySelector('.validation-text');
    
    // Reset classes
    element.className = 'validation-status';
    element.classList.add(status);
    
    // Update icon based on status
    icon.className = 'validation-icon';
    switch(status) {
        case 'valid':
            icon.classList.add('fa-circle-check');
            break;
        case 'warning':
            icon.classList.add('fa-exclamation-triangle');
            break;
        case 'error':
            icon.classList.add('fa-circle-xmark');
            break;
        default:
            icon.classList.add('fa-circle-check');
    }
    
    // Update text
    textElement.textContent = text;
}

// Update validation message display
function updateValidationMessage(elementId, result) {
    const element = document.getElementById(elementId);
    if (result.valid) {
        element.textContent = '';
        element.className = 'validation-message';
    } else {
        element.textContent = result.details || result.message;
        element.className = 'validation-message error';
    }
}

// Perform the transformation
function performTransform() {
    const input = inputEditor.getValue();
    const spec = specEditor.getValue();
    
    // Validate that we have input
    if (!input.trim()) {
        showNotification('Please provide input JSON data', 'warning');
        return;
    }
    
    // Validate that we have spec
    if (!spec.trim()) {
        showNotification('Please provide a transformation specification', 'warning');
        return;
    }
    
    // Show loading overlay
    showLoadingOverlay();
    
    // Prepare transform request - only support chain operations
    const requestData = {
        operation: 'chain'
    };
    
    // Add spec to request (only support chain operations)
    try {
        requestData.chainSpec = JSON.parse(spec);
        requestData.input = input;
    } catch (e) {
        requestData.spec = spec;
        requestData.input = input;
    }
    
    // Send transform request to backend
    fetch('/api/transform', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
    .then(response => response.json())
    .then(result => {
        hideLoadingOverlay();
        
        if (result.success) {
            // Display result
            outputEditor.setValue(result.result || '');
            
            // Update execution time
            document.getElementById('executionTime').textContent = result.executionTime + 'ms';
            
            // Update complexity indicator
            updateComplexityIndicator(result.complexity);
            
            // Update validation status
            updateValidationStatus('outputValidation', 'Success', 'valid');
            updateValidationMessage('outputValidationMessage', {
                valid: true,
                message: 'Transformation successful'
            });
            
            showNotification('Transformation completed successfully', 'success');
        } else {
            // Display error
            outputEditor.setValue('');
            
            // Update validation status
            updateValidationStatus('outputValidation', 'Error', 'error');
            updateValidationMessage('outputValidationMessage', {
                valid: false,
                message: 'Transformation failed',
                details: result.error
            });
            
            showNotification('Transformation failed: ' + result.error, 'error');
        }
    })
    .catch(error => {
        hideLoadingOverlay();
        
        // Display error
        outputEditor.setValue('');
        
        // Update validation status
        updateValidationStatus('outputValidation', 'Error', 'error');
        updateValidationMessage('outputValidationMessage', {
            valid: false,
            message: 'Request failed',
            details: error.message
        });
        
        showNotification('Request failed: ' + error.message, 'error');
    });
}

// Update complexity indicator
function updateComplexityIndicator(complexity) {
    const indicator = document.getElementById('complexityIndicator');
    indicator.innerHTML = `<i class="fas fa-tachometer-alt"></i> <span>Complexity: ${complexity || 'Unknown'}</span>`;
    
    // Add class based on complexity
    indicator.className = 'status-item';
    if (complexity === 'High') {
        indicator.classList.add('high-complexity');
    } else if (complexity === 'Medium') {
        indicator.classList.add('medium-complexity');
    } else if (complexity === 'Low') {
        indicator.classList.add('low-complexity');
    }
}

// Format input JSON
function formatInput() {
    const input = inputEditor.getValue();
    if (!input.trim()) return;
    
    try {
        const parsed = JSON.parse(input);
        const formatted = JSON.stringify(parsed, null, 2);
        inputEditor.setValue(formatted);
        showNotification('Input JSON formatted successfully', 'success');
    } catch (e) {
        showNotification('Invalid JSON: ' + e.message, 'error');
    }
}

// Format specification JSON
function formatSpec() {
    const spec = specEditor.getValue();
    if (!spec.trim()) return;
    
    try {
        const parsed = JSON.parse(spec);
        const formatted = JSON.stringify(parsed, null, 2);
        specEditor.setValue(formatted);
        showNotification('Specification formatted successfully', 'success');
    } catch (e) {
        showNotification('Invalid JSON: ' + e.message, 'error');
    }
}

// Clear input editor
function clearInput() {
    inputEditor.setValue('');
    updateValidationStatus('inputValidation', 'Ready', 'valid');
    updateValidationMessage('inputValidationMessage', { valid: true });
    showNotification('Input cleared', 'info');
}

// Clear specification editor
function clearSpec() {
    specEditor.setValue('');
    updateValidationStatus('specValidation', 'Ready', 'valid');
    updateValidationMessage('specValidationMessage', { valid: true });
    showNotification('Specification cleared', 'info');
}

// Copy output to clipboard
function copyOutput() {
    const output = outputEditor.getValue();
    if (!output.trim()) {
        showNotification('No output to copy', 'warning');
        return;
    }
    
    navigator.clipboard.writeText(output)
        .then(() => {
            showNotification('Output copied to clipboard', 'success');
        })
        .catch(err => {
            showNotification('Failed to copy output: ' + err.message, 'error');
        });
}

// Download output as JSON file
function downloadOutput() {
    const output = outputEditor.getValue();
    if (!output.trim()) {
        showNotification('No output to download', 'warning');
        return;
    }
    
    try {
        // Parse and re-stringify to ensure valid JSON
        const parsed = JSON.parse(output);
        const json = JSON.stringify(parsed, null, 2);
        
        // Create blob and download
        const blob = new Blob([json], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'j2j-output.json';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
        
        showNotification('Output downloaded successfully', 'success');
    } catch (e) {
        showNotification('Invalid JSON output: ' + e.message, 'error');
    }
}

// Toggle view between editor and tree view
function toggleView() {
    const editor = document.getElementById('outputEditor').parentElement;
    const treeView = document.getElementById('treeView');
    
    if (editor.style.display === 'none') {
        editor.style.display = 'block';
        treeView.style.display = 'none';
        document.getElementById('toggleViewBtn').innerHTML = '<i class="fas fa-eye"></i>';
    } else {
        editor.style.display = 'none';
        treeView.style.display = 'block';
        document.getElementById('toggleViewBtn').innerHTML = '<i class="fas fa-code"></i>';
        
        // Render tree view
        renderTreeView();
    }
}

// Render tree view of output
function renderTreeView() {
    const output = outputEditor.getValue();
    if (!output.trim()) {
        document.getElementById('treeView').innerHTML = '<p>No data to display</p>';
        return;
    }
    
    try {
        const parsed = JSON.parse(output);
        const treeHtml = renderJsonTree(parsed, 0);
        document.getElementById('treeView').innerHTML = treeHtml;
    } catch (e) {
        document.getElementById('treeView').innerHTML = '<p>Invalid JSON: ' + e.message + '</p>';
    }
}

// Render JSON as tree structure
function renderJsonTree(obj, depth) {
    if (obj === null) return '<span class="json-null">null</span>';
    if (obj === undefined) return '<span class="json-undefined">undefined</span>';
    
    const type = typeof obj;
    
    if (type === 'string') {
        return `<span class="json-string">"${obj}"</span>`;
    }
    
    if (type === 'number') {
        return `<span class="json-number">${obj}</span>`;
    }
    
    if (type === 'boolean') {
        return `<span class="json-boolean">${obj}</span>`;
    }
    
    if (Array.isArray(obj)) {
        if (obj.length === 0) return '<span class="json-array">[]</span>';
        
        let html = '<div class="json-array">[\n';
        for (let i = 0; i < obj.length; i++) {
            html += '  '.repeat(depth + 1) + renderJsonTree(obj[i], depth + 1);
            if (i < obj.length - 1) html += ',';
            html += '\n';
        }
        html += '  '.repeat(depth) + ']</div>';
        return html;
    }
    
    // Object
    const keys = Object.keys(obj);
    if (keys.length === 0) return '<span class="json-object">{}</span>';
    
    let html = '<div class="json-object">{\n';
    for (let i = 0; i < keys.length; i++) {
        const key = keys[i];
        html += '  '.repeat(depth + 1) + `<span class="json-key">"${key}"</span>: ` + renderJsonTree(obj[key], depth + 1);
        if (i < keys.length - 1) html += ',';
        html += '\n';
    }
    html += '  '.repeat(depth) + '}</div>';
    return html;
}

// Show tools modal
function showToolsModal() {
    document.getElementById('toolsModal').classList.add('show');
}

// Close tools modal
function closeToolsModal() {
    document.getElementById('toolsModal').classList.remove('show');
}

// Show settings modal
function showSettingsModal() {
    document.getElementById('settingsModal').classList.add('show');
}

// Close settings modal
function closeSettingsModal() {
    document.getElementById('settingsModal').classList.remove('show');
}

// Show loading overlay
function showLoadingOverlay() {
    document.getElementById('loadingOverlay').classList.add('show');
}

// Hide loading overlay
function hideLoadingOverlay() {
    document.getElementById('loadingOverlay').classList.remove('show');
}

// Show notification
function showNotification(message, type = 'info') {
    const container = document.getElementById('notifications');
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <i class="fas fa-${getNotificationIcon(type)}"></i>
        <span>${message}</span>
        <button class="notification-close" onclick="this.parentElement.remove()"><i class="fas fa-times"></i></button>
    `;
    
    container.appendChild(notification);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 5000);
}

// Get icon for notification type
function getNotificationIcon(type) {
    switch(type) {
        case 'success': return 'check-circle';
        case 'error': return 'exclamation-circle';
        case 'warning': return 'exclamation-triangle';
        case 'info': return 'info-circle';
        default: return 'info-circle';
    }
}

// Load examples for different operations
function loadExamples() {
    // This function can be expanded to load default examples
    // For now, we'll just set up the event listeners
}

// Load chain example
function loadChainExample() {
    const input = `{
  "rating": {
    "primary": {
      "value": 3
    },
    "quality": {
      "value": 3
    }
  }
}`;
    
    const spec = `[
  {
    "operation": "shift",
    "spec": {
      "rating": {
        "primary": {
          "value": "Rating"
        },
        "*": {
          "value": "SecondaryRatings.&1.Value",
          "Id": "SecondaryRatings.&1.Id"
        }
      }
    }
  },
  {
    "operation": "default",
    "spec": {
      "RatingRange": 5,
      "SecondaryRatings": {
        "*": {
          "Range": 5
        }
      }
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load simple shift example
function loadSimpleShiftExample() {
    const input = `{
  "rating": {
    "primary": {
      "value": 4,
      "max": 5
    },
    "quality": {
      "value": 3,
      "max": 7
    }
  }
}`;
    
    const spec = `[
  {
    "operation": "shift",
    "spec": {
      "rating": {
        "primary": {
          "value": "Rating",
          "max": "RatingRange"
        },
        "*": {
          "value": "SecondaryRatings.&1.Value",
          "max": "SecondaryRatings.&1.Range",
          "$": "SecondaryRatings.&1.Id"
        }
      }
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load attribute reference example
function loadAttributeReferenceExample() {
    const input = `{
  "rating": {
    "primary": {
      "value": 3,
      "max": 5
    }
  }
}`;
    
    const spec = `[
  {
    "operation": "shift",
    "spec": {
      "rating": {
        "primary": {
          "value": "Rating"
        }
      }
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load default values example
function loadDefaultValuesExample() {
    const input = `{
  "Rating": 3
}`;
    
    const spec = `[
  {
    "operation": "default",
    "spec": {
      "RatingRange": 5
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load identity example
function loadIdentityExample() {
    const input = `{
  "message": "Hello World",
  "count": 42
}`;
    
    const spec = `[
  {
    "operation": "shift",
    "spec": {
      "@": ""
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load array handling example
function loadArrayHandlingExample() {
    const input = `{
  "users": [
    {
      "name": "John"
    },
    {
      "name": "Jane"
    }
  ]
}`;
    
    const spec = `[
  {
    "operation": "shift",
    "spec": {
      "users": {
        "*": {
          "name": "names[]"
        }
      }
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load array filtering example
function loadArrayFilteringExample() {
    const input = `{
  "numbers": [
    1,
    2,
    3,
    4,
    5,
    6,
    7,
    8,
    9,
    10
  ]
}`;
    
    const spec = `[
  {
    "operation": "shift",
    "spec": {
      "numbers": {
        "2": "third",
        "5": "sixth",
        "8": "ninth"
      }
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load nested arrays example
function loadNestedArraysExample() {
    const input = `{
  "departments": [
    {
      "name": "IT",
      "employees": [
        {
          "name": "John"
        },
        {
          "name": "Jane"
        }
      ]
    },
    {
      "name": "HR",
      "employees": [
        {
          "name": "Bob"
        }
      ]
    }
  ]
}`;
    
    const spec = `[
  {
    "operation": "shift",
    "spec": {
      "departments": {
        "*": {
          "name": "depts[&1].name",
          "employees": {
            "*": {
              "name": "depts[&3].employees[&1].name"
            }
          }
        }
      }
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load wildcards example
function loadWildcardsExample() {
    const input = `{
  "data": {
    "item1": "value1",
    "item2": "value2"
  }
}`;
    
    const spec = `[
  {
    "operation": "shift",
    "spec": {
      "data": {
        "*": "items.&"
      }
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load complex nested example
function loadComplexNestedExample() {
    const input = `{
  "level1": {
    "level2": {
      "level3": {
        "value": "deep"
      }
    }
  }
}`;
    
    const spec = `[
  {
    "operation": "shift",
    "spec": {
      "level1": {
        "level2": {
          "level3": {
            "value": "deepValue"
          }
        }
      }
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load multi-operation example
function loadMultiOperationExample() {
    const input = `{
  "rating": {
    "primary": {
      "value": 3
    },
    "quality": {
      "value": 4
    }
  }
}`;
    
    const spec = `[
  {
    "operation": "shift",
    "spec": {
      "rating": {
        "primary": {
          "value": "primaryRating"
        }
      }
    }
  },
  {
    "operation": "default",
    "spec": {
      "primaryRatingMax": 5
    }
  },
  {
    "operation": "remove",
    "spec": {
      "rating": {
        "quality": ""
      }
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load boolean values example
function loadBooleanValuesExample() {
    const input = `{
  "flag": true,
  "status": false
}`;
    
    const spec = `[
  {
    "operation": "shift",
    "spec": {
      "flag": "flagResult",
      "status": "statusResult"
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load null values example
function loadNullValuesExample() {
    const input = `{
  "key": null
}`;
    
    const spec = `[
  {
    "operation": "shift",
    "spec": {
      "key": "value"
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load number values example
function loadNumberValuesExample() {
    const input = `{
  "integer": 42,
  "float": 3.14,
  "negative": -10
}`;
    
    const spec = `[
  {
    "operation": "shift",
    "spec": {
      "integer": "intResult",
      "float": "floatResult",
      "negative": "negResult"
    }
  }
]`;
    
    inputEditor.setValue(input);
    specEditor.setValue(spec);
}

// Load generic example
function loadGenericExample() {
    const input = `{
  "key": "value"
}`;
    
    inputEditor.setValue(input);
    specEditor.setValue('');
}