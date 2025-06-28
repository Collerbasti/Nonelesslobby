Get-ChildItem -Path "src/main/java" -Filter "*.java" -Recurse | ForEach-Object {
    $content = Get-Content $_.FullName -Encoding UTF8
    [System.IO.File]::WriteAllLines($_.FullName, $content, [System.Text.UTF8Encoding]::new($false))
} 