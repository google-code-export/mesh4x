;SetCompressor /SOLID lzma
;SetCompressorDictSize 16

;--------------------------------------------------------
; Includes
;--------------------------------------------------------

!include "MUI.nsh"

;--------------------------------------------------------
; General definitions
;--------------------------------------------------------

; Name of the installer
Name "Meshes"

OutFile "Meshes_Install.exe"

; Where is the program going to be installed
InstallDir "$PROGRAMFILES\InSTEDD\Meshes"

; Grab the directory from the registry, if possible
InstallDirRegKey HKCU "Software\InSTEDD\Meshes\1.0" ""

; So no one can corrput the installer
CRCCheck force

;--------------------------------------------------------
; Interface settings
;--------------------------------------------------------

; Confirmation before quitting the installer
!define MUI_ABORTWARNING

;--------------------------------------------------------
; Language selection dialog settings
;--------------------------------------------------------

; Remember the installation language
!define MUI_LANGDLL_REGISTRY_ROOT "HKCU"
!define MUI_LANGDLL_REGISTRY_KEY "Software\InSTEDD\Meshes\1.0"
!define MUI_LANGDLL_REGISTRY_VALUENAME "Installer Language"
#!define MUI_FINISHPAGE_RUN "$INSTDIR\Meshes.jar"

;--------------------------------------------------------
; Installer pages
;--------------------------------------------------------

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

;--------------------------------------------------------
; Languages
;--------------------------------------------------------

!insertmacro MUI_LANGUAGE "English"

;--------------------------------------------------------
; Reservar languages needed by the installer
;--------------------------------------------------------

!insertmacro MUI_RESERVEFILE_LANGDLL

;--------------------------------------------------------
; Required section: main program files, registry keys, etc.
;--------------------------------------------------------

Section "Meshes (required)" MainFiles
    ; This section is mandatory
    SectionIn RO

    ; Output path
    SetOutPath $INSTDIR

    ; The program directories
    CreateDirectory "$INSTDIR"
    
    ; The files
    File Meshes.jar
    
    ; Output path
    SetOutPath $INSTDIR
    
    ; Associate .mesh files with the program
    WriteRegStr HKCU SOFTWARE\Classes\InsTEDD.Meshes\shell\open\command "" 'javaw -jar "$INSTDIR\Meshes.jar" "%1"'
    WriteRegStr HKCU SOFTWARE\Classes\.mesh "" "InsTEDD.Meshes"

    ; Write installation directory in the registry
    WriteRegStr HKLM SOFTWARE\InSTEDD\Meshes\1.0 "Install_Dir" "$INSTDIR"
    
    ; Write registry keys for the uninstaller
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Meshes" "DisplayName" "Meshes"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Meshes" "UninstallString" '"$INSTDIR\uninstall.exe"'
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Meshes" "NoModify" 1
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Meshes" "NoRepair" 1
    WriteUninstaller "uninstall.exe"
SectionEnd

Section "Start Menu Shortcuts" StartMenuShortcuts
    CreateDirectory "$SMPROGRAMS\InSTEDD\Meshes"
    CreateShortCut "$SMPROGRAMS\InSTEDD\Meshes\$(SHORTCUT_Uninstall).lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
    CreateShortCut "$SMPROGRAMS\InSTEDD\Meshes\Meshes.lnk" "$INSTDIR\Meshes.jar" "" "$INSTDIR\Meshes.jar" 0
SectionEnd

Section "Desktop Shortcuts" DesktopShortcuts
    CreateShortcut "$DESKTOP\Meshes.lnk" "$INSTDIR\Meshes.jar" "" "$INSTDIR\Meshes.jar" 0
SectionEnd

;--------------------------------------------------------
; Installer functions
;--------------------------------------------------------

Function .onInit
    ; Commented for now because there's only one language
    ;!insertmacro MUI_LANGDLL_DISPLAY
FunctionEnd

; Contains the descriptions of components and other stuff
!include Meshes_Descriptions.nsh

;--------------------------------------------------------
; Uninstaller
;--------------------------------------------------------

Section "Uninstall"

    ; Delete registry keys
    DeleteRegKey HKCU "SOFTWARE\Classes\InsTEDD.Meshes\shell\open\command"
    DeleteRegKey HKCU "SOFTWARE\Classes\InsTEDD.Meshes\shell\open"
    DeleteRegKey HKCU "SOFTWARE\Classes\InsTEDD.Meshes\shell"
    DeleteRegKey HKCU "SOFTWARE\Classes\InsTEDD.Meshes"
    DeleteRegKey HKCU "SOFTWARE\Classes\.mesh"
    
    DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Meshes"
    DeleteRegKey HKLM SOFTWARE\InSTEDD\Meshes\1.0
    DeleteRegKey HKLM SOFTWARE\InSTEDD\Meshes
    DeleteRegKey /ifempty HKLM SOFTWARE\InSTEDD

    ; This is to delete the language rememebred by the installer
    DeleteRegKey HKCU Software\InSTEDD\Meshes
    DeleteRegKey /ifempty HKCU Software\InSTEDD

    ; Delete Meshes.jar
    Delete $INSTDIR\Meshes.jar
    Delete $INSTDIR

    ; Delete the uninstaller
    Delete $INSTDIR\uninstall.exe

    ; Delete the shortcuts, if any
    Delete "$SMPROGRAMS\InSTEDD\Meshes\*.*"
    Delete "$SMPROGRAMS\InSTEDD\Meshes"
    Delete "$SMPROGRAMS\InSTEDD"
    Delete "$DESKTOP\Meshes.lnk"

    ; Delete used directories
    RMDir "$SMPROGRAMS\InSTEDD\Meshes"

SectionEnd

;--------------------------------------------------------
; Uninstaller functions
;--------------------------------------------------------

Function un.onInit
    ; Ask the language when the installer begins

    ; Currently commented because there's only one langauge
    ;!insertmacro MUI_UNGETLANGUAGE
FunctionEnd
