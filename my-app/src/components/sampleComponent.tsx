import React from 'react';

// Define a generic Props type for your component
interface MyComponentProps<T> {
  data: T;
}

// Create a generic functional component
function MyComponent<T>({ data }: MyComponentProps<T>) {
  return (
    <div>
      <h1>My Generic Component</h1>
      <p>Data: {JSON.stringify(data)}</p>
    </div>
  );
}

// Export the generic component
export default MyComponent;
