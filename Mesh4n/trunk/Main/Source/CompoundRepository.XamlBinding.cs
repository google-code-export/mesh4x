namespace SimpleSharing
{
    using System;
    using System.ComponentModel;
    
    
    [System.CodeDom.Compiler.GeneratedCodeAttribute("XamlBindingTool", "1.0.0.0")]
    public partial class CompoundRepository : ISupportInitialize, ISupportInitializeNotification, IChangeTracking, INotifyPropertyChanged
    {
        
        const string InitializationNotBegun = "Initialization has not been started.";
        
        const string NotInitialized = "The object has not been initialized properly. Call Initialize prior to use.";
        
        private bool _beginCalled;
        
        private bool _isInitialized;
        
        private bool _isChanged;
        
        private bool RequiresInitialize
        {
            get
            {
                return ((((ISupportInitializeNotification)(this)).IsInitialized == false) 
                            || ((IChangeTracking)(this)).IsChanged);
            }
        }
        
        bool ISupportInitializeNotification.IsInitialized
        {
            get
            {
                return (this._isInitialized && this.AreNestedInitialized);
            }
        }
        
        ///  <summary>Gets whether the object properties that support <see cref='ISupportInitializeNotification'/> are initialized.</summary>
        [EditorBrowsableAttribute(EditorBrowsableState.Never)]
        protected virtual bool AreNestedInitialized
        {
            get
            {
                return ((true && this.IsValueInitialized(XmlRepository as ISupportInitializeNotification)) 
                            && this.IsValueInitialized(SyncRepository as ISupportInitializeNotification));
            }
        }
        
        bool IChangeTracking.IsChanged
        {
            get
            {
                return (this._isChanged || this.HasNestedChanges);
            }
        }
        
        /// <summary>Gets whether the object properties that support <see cref='IChangeTracking'/> report changes.</summary>
        [EditorBrowsableAttribute(EditorBrowsableState.Never)]
        protected virtual bool HasNestedChanges
        {
            get
            {
                return ((false || this.IsValueChanged(XmlRepository as IChangeTracking)) 
                            || this.IsValueChanged(SyncRepository as IChangeTracking));
            }
        }
        
        /// <summary>Signals that the object has been initialized.</summary>
        public event EventHandler Initialized;
        
        /// <summary>Signals that a property has changed.</summary>
        public event PropertyChangedEventHandler PropertyChanged;
        
        /// <summary>Signals that the property <see cref='XmlRepository'/> has changed.</summary>
        public event EventHandler XmlRepositoryChanged;
        
        /// <summary>Signals that the property <see cref='SyncRepository'/> has changed.</summary>
        public event EventHandler SyncRepositoryChanged;
        
        /// <summary>Validates object properties and initializes it for use.</summary>
        public void Initialize()
        {
            if (this.RequiresInitialize)
            {
                this.DoValidate();
                this._isChanged = false;
                this.InitializeNested();
                this.DoInitialize();
                this._isInitialized = true;
                if ((this.Initialized != null))
                {
                    this.Initialized(this, EventArgs.Empty);
                }
            }
        }
        
        /// <summary>Initializes the nested properties that support <see cref='ISupportInitialize'/>.</summary>
        [EditorBrowsableAttribute(EditorBrowsableState.Never)]
        protected virtual void InitializeNested()
        {
            ISupportInitialize initializable;
            initializable = XmlRepository as ISupportInitialize;
            if ((initializable != null))
            {
                initializable.BeginInit();
                initializable.EndInit();
            }
            initializable = SyncRepository as ISupportInitialize;
            if ((initializable != null))
            {
                initializable.BeginInit();
                initializable.EndInit();
            }
        }
        
        void ISupportInitialize.BeginInit()
        {
            this._beginCalled = true;
        }
        
        void ISupportInitialize.EndInit()
        {
            if ((this._beginCalled == false))
            {
                throw new InvalidOperationException(CompoundRepository.InitializationNotBegun);
            }
            this.Initialize();
        }
        
        /// <summary>Determines whether the value is null or its 
        ///				<see cref='ISupportInitializeNotification.IsInitialized'/> is <see langword='true' />.
        ///				</summary>
        [EditorBrowsableAttribute(EditorBrowsableState.Never)]
        protected bool IsValueInitialized(ISupportInitializeNotification child)
        {
            return ((child == null) 
                        || child.IsInitialized);
        }
        
        void IChangeTracking.AcceptChanges()
        {
            this.Initialize();
        }
        
        /// <summary>Determines whether the value is not null and has changed.</summary>
        [EditorBrowsableAttribute(EditorBrowsableState.Never)]
        protected bool IsValueChanged(IChangeTracking value)
        {
            return ((value != null) 
                        && value.IsChanged);
        }
        
        /// <summary>Raises the <see cref='PropertyChanged'/> event.</summary>
        ///				  <param name='property'>Name of the property that changed.</param>
        [EditorBrowsableAttribute(EditorBrowsableState.Never)]
        protected void RaisePropertyChanged(string property)
        {
            if ((this.PropertyChanged != null))
            {
                this.PropertyChanged(this, new PropertyChangedEventArgs(property));
            }
            this._isChanged = true;
        }
        
        /// <summary>
        ///Checks that the object has been properly initialized through 
        ///calls to <see cref='ISupportInitialize.BeginInit'/> and 
        ///<see cref='ISupportInitialize.EndInit'/> or the <see cref='Initialize'/> method.
        ///</summary>
        ///<exception cref='InvalidOperationException'>The object was not initialized.</exception>
        protected void EnsureInitialized()
        {
            if (this.RequiresInitialize)
            {
                throw new InvalidOperationException(CompoundRepository.NotInitialized);
            }
        }
        
        /// <summary>Raises the <see cref='XmlRepositoryChanged'/> event.</summary>
        private void RaiseXmlRepositoryChanged()
        {
            if ((this.XmlRepositoryChanged != null))
            {
                this.XmlRepositoryChanged(this, EventArgs.Empty);
            }
            this.RaisePropertyChanged("XmlRepository");
        }
        
        /// <summary>Raises the <see cref='SyncRepositoryChanged'/> event.</summary>
        private void RaiseSyncRepositoryChanged()
        {
            if ((this.SyncRepositoryChanged != null))
            {
                this.SyncRepositoryChanged(this, EventArgs.Empty);
            }
            this.RaisePropertyChanged("SyncRepository");
        }
    }
}
