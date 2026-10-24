# Inspect Chrome's native UI and optionally invoke a recognized dismissal button. Never print accessibility text, which can contain credentials.
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName UIAutomationClient
Add-Type -AssemblyName UIAutomationTypes
$dismiss = '__DISMISS__' -eq 'true'
$chromeProcessId = [int]'__CHROME_PID__'
$deadline = [DateTime]::UtcNow.AddSeconds([int]'__TIMEOUT_SECONDS__')
$walker = [System.Windows.Automation.TreeWalker]::ControlViewWalker
$promptTitles = @('Save password?', 'Save password', 'Update password?', 'Update password',
'Change your password', 'Check your passwords')
function Find-PasswordPrompt($element, [int]$depth)
{
    if ($depth -gt 30 -or [DateTime]::UtcNow -ge $deadline)
    {
        return $null
    }
    try
    {
        $current = $element.Current
        # Exclude web page content: a page label must not satisfy the native prompt wait.
        if ($current.ControlType -eq [System.Windows.Automation.ControlType]::Document)
        {
            return $null
        }
        if (-not $current.IsOffscreen -and $promptTitles -contains $current.Name)
        {
            return $element
        }
        $child = $walker.GetFirstChild($element)
        while ($null -ne $child)
        {
            $found = Find-PasswordPrompt $child ($depth + 1)
            if ($null -ne $found)
            {
                return $found
            }
            $child = $walker.GetNextSibling($child)
        }
    }
    catch [System.Windows.Automation.ElementNotAvailableException]
    {
        # Chrome can rebuild native UI between polling attempts.
    }
    return $null
}
# Never invoke Save/Update/Change password or Chrome's own Close button.
$dismissLabels = @('OK', 'Got it', 'Not now', 'Never', 'No thanks', 'Cancel')
function Find-DismissButton($element, [int]$depth)
{
    if ($depth -gt 15 -or [DateTime]::UtcNow -ge $deadline)
    {
        return $null
    }
    $current = $element.Current
    if ($current.ControlType -eq [System.Windows.Automation.ControlType]::Document)
    {
        return $null
    }
    if ($current.ControlType -eq [System.Windows.Automation.ControlType]::Button -and
            -not $current.IsOffscreen -and $current.IsEnabled -and $dismissLabels -contains $current.Name)
    {
        return $element
    }
    $child = $walker.GetFirstChild($element)
    while ($null -ne $child)
    {
        $found = Find-DismissButton $child ($depth + 1)
        if ($null -ne $found)
        {
            return $found
        }
        $child = $walker.GetNextSibling($child)
    }
    return $null
}
function Dismiss-PasswordPrompt($title, $window)
{
    $container = $walker.GetParent($title)
    while ($null -ne $container -and -not $container.Equals($window))
    {
        $button = Find-DismissButton $container 0
        if ($null -ne $button)
        {
            $pattern = $button.GetCurrentPattern([System.Windows.Automation.InvokePattern]::Pattern)
            $pattern.Invoke()
            return $true
        }
        $container = $walker.GetParent($container)
    }
    return $false
}
while ([DateTime]::UtcNow -lt $deadline)
{
    $process = Get-Process -Id $chromeProcessId -ErrorAction Stop
    if ($process.ProcessName -ne 'chrome')
    {
        exit 3
    }
    $condition = New-Object System.Windows.Automation.PropertyCondition(
    [System.Windows.Automation.AutomationElement]::ProcessIdProperty, [int]$chromeProcessId)
    $windows = [System.Windows.Automation.AutomationElement]::RootElement.FindAll(
            [System.Windows.Automation.TreeScope]::Children, $condition)
    foreach ($window in $windows)
    {
        $prompt = Find-PasswordPrompt $window 0
        if ($null -ne $prompt)
        {
            if (-not $dismiss)
            {
                exit 0
            }
            if (-not (Dismiss-PasswordPrompt $prompt $window))
            {
                exit 4
            }
            do
            {
                $remaining = Find-PasswordPrompt $window 0
                if ([DateTime]::UtcNow -lt $deadline -and $null -eq $remaining)
                {
                    exit 0
                }
                Start-Sleep -Milliseconds 100
            } while ([DateTime]::UtcNow -lt $deadline)
            exit 4
        }
    }
    if ($dismiss)
    {
        exit 2
    }
    Start-Sleep -Milliseconds 250
}
exit 2
