;--------------------------------------------------------
; English
;--------------------------------------------------------

; Sections
LangString DESC_MainFiles ${LANG_ENGLISH} "Required Meshes program files."
LangString DESC_StartMenuShortcuts ${LANG_ENGLISH} "Meshes shortcuts in the Start Menu."
LangString DESC_DesktopShortcuts ${LANG_ENGLISH} "Meshes shortcuts in the Desktop."

; Shortcuts
LangString SHORTCUT_Uninstall ${LANG_ENGLISH} "Uninstall"

;--------------------------------------------------------
; Assign texts to sections
;--------------------------------------------------------

!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${MainFiles} $(DESC_MainFiles)
    !insertmacro MUI_DESCRIPTION_TEXT ${StartMenuShortcuts} $(DESC_StartMenuShortcuts)
    !insertmacro MUI_DESCRIPTION_TEXT ${DesktopShortcuts} $(DESC_DesktopShortcuts)
!insertmacro MUI_FUNCTION_DESCRIPTION_END
