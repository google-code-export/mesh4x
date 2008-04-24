namespace SimpleSharing.Data
{
    using System;
    using System.ComponentModel;
    
    
    [System.CodeDom.Compiler.GeneratedCodeAttribute("XamlBindingTool", "1.0.0.0")]
    public partial class DbSyncRepository
    {
        
        /// <summary>Gets whether the object properties that support <see cref='IChangeTracking'/> report changes.</summary>
        [EditorBrowsableAttribute(EditorBrowsableState.Never)]
        protected override bool HasNestedChanges
        {
            get
            {
                return base.HasNestedChanges;
            }
        }
        
        ///  <summary>Gets whether the object properties that support <see cref='ISupportInitializeNotification'/> are initialized.</summary>
        [EditorBrowsableAttribute(EditorBrowsableState.Never)]
        protected override bool AreNestedInitialized
        {
            get
            {
                return base.AreNestedInitialized;
            }
        }
        
        /// <summary>Signals that the property <see cref='RepositoryId'/> has changed.</summary>
        public event EventHandler RepositoryIdChanged;
        
        /// <summary>Initializes the nested properties that support <see cref='ISupportInitialize'/>.</summary>
        [EditorBrowsableAttribute(EditorBrowsableState.Never)]
        protected override void InitializeNested()
        {
            base.InitializeNested();
            ISupportInitialize initializable;
        }
        
        /// <summary>Raises the <see cref='RepositoryIdChanged'/> event.</summary>
        private void RaiseRepositoryIdChanged()
        {
            if ((this.RepositoryIdChanged != null))
            {
                this.RepositoryIdChanged(this, EventArgs.Empty);
            }
            this.RaisePropertyChanged("RepositoryId");
        }
    }
}
